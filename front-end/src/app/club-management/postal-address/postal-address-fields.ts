import { Component, DestroyRef, WritableSignal, computed, effect, inject, input, signal, untracked } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { catchError, filter, of } from 'rxjs';
import { AddressProblem, PostalAddress, isPostcode } from './postal-address';
import { DeliveryTown, Towns } from '../ports/towns';

/**
 * The fields of a postal address, always laid out the same way. The problems are shown as given:
 * the enclosing form decides when the address is checked.
 */
@Component({
  selector: 'app-postal-address-fields',
  styleUrl: './postal-address-fields.css',
  template: `
    @let id = idPrefix();
    <div class="field field--wide">
      <label [for]="id + '-street'" class="required">Numéro et voie</label>
      <input
        [id]="id + '-street'"
        aria-required="true"
        [attr.autocomplete]="'section-' + id + ' address-line1'"
        placeholder="ex. 12 rue des Échecs"
        [value]="given().street"
        [attr.aria-invalid]="problems().has('missingStreet') || null"
        [attr.aria-describedby]="problems().has('missingStreet') ? id + '-street-error' : null"
        (input)="describe({ street: street.value })"
        #street
      />
      @if (problems().has('missingStreet')) {
        <p [id]="id + '-street-error'" class="field-error">Le numéro et la voie sont obligatoires.</p>
      }
    </div>
    <div class="field">
      <label [for]="id + '-postcode'" class="required">Code postal</label>
      <input
        [id]="id + '-postcode'"
        aria-required="true"
        [attr.autocomplete]="'section-' + id + ' postal-code'"
        inputmode="numeric"
        [attr.list]="id + '-postcodes'"
        placeholder="ex. 45000"
        [value]="given().postcode"
        [attr.aria-invalid]="problems().has('missingPostcode') || problems().has('malformedPostcode') || null"
        [attr.aria-describedby]="problems().has('missingPostcode') || problems().has('malformedPostcode') ? id + '-postcode-error' : null"
        (input)="typePostcode(postcode.value)"
        #postcode
      />
      @if (problems().has('missingPostcode')) {
        <p [id]="id + '-postcode-error'" class="field-error">Le code postal est obligatoire.</p>
      } @else if (problems().has('malformedPostcode')) {
        <p [id]="id + '-postcode-error'" class="field-error">Le code postal doit comporter 5 chiffres.</p>
      }
      <datalist [id]="id + '-postcodes'">
        @for (serving of servingPostcodes(); track serving) {
          <option [value]="serving"></option>
        }
      </datalist>
    </div>
    <div class="field">
      <label [for]="id + '-town'" class="required">Localité</label>
      <input
        [id]="id + '-town'"
        aria-required="true"
        [attr.autocomplete]="'section-' + id + ' address-level2'"
        [attr.list]="id + '-towns'"
        placeholder="ex. Orléans"
        [value]="given().town"
        [attr.aria-invalid]="problems().has('missingTown') || null"
        [attr.aria-describedby]="problems().has('missingTown') ? id + '-town-error' : null"
        (input)="typeTown(town.value)"
        #town
      />
      @if (problems().has('missingTown')) {
        <p [id]="id + '-town-error'" class="field-error">La localité est obligatoire.</p>
      }
      <datalist [id]="id + '-towns'">
        @for (served of servedTowns(); track served) {
          <option [value]="served"></option>
        }
      </datalist>
    </div>
  `,
})
export class PostalAddressFields {
  /** Makes the ids of the fields unique when a form holds several addresses. */
  readonly idPrefix = input.required<string>();
  /**
   * The address, owned by the enclosing form: the fields read and change it in place, so that a late
   * answer always meets the address as it is, even before the fields are refreshed.
   */
  readonly address = input.required<WritableSignal<PostalAddress>>();
  protected readonly given = computed(() => this.address()());
  readonly problems = input<ReadonlySet<AddressProblem>>(new Set());
  /** The town of the commune of the club, proposed until the administrator gives another. */
  readonly defaultTown = input<DeliveryTown | null>(null);

  private readonly towns = inject(Towns);
  private readonly destroyRef = inject(DestroyRef);
  // A suggestion only helps with the address as it is: it fades once what it was found for changes.
  private readonly townsFound = signal<{ postcode: string; towns: readonly string[] }>({ postcode: '', towns: [] });
  protected readonly servedTowns = computed(() => {
    const found = this.townsFound();
    return found.postcode === this.given().postcode ? found.towns : [];
  });
  private readonly postcodesFound = signal<{ town: string; postcodes: readonly string[] }>({ town: '', postcodes: [] });
  protected readonly servingPostcodes = computed(() => {
    const found = this.postcodesFound();
    return found.town === this.given().town ? found.postcodes : [];
  });
  // What the commune last proposed: the administrator has not changed it while the address still holds it.
  private proposed = { town: '', postcode: '' };

  constructor() {
    // Only a change of commune proposes its town: the administrator's own changes do not.
    effect(() => {
      const town = this.defaultTown();
      if (town) untracked(() => this.propose(town));
    });
  }

  private propose(town: DeliveryTown): void {
    const given = this.given();
    const proposed = { town: town.name, postcode: town.postcodes.length === 1 ? town.postcodes[0] : '' };
    if (!given.town || given.town === this.proposed.town) this.describe({ town: proposed.town });
    if (!given.postcode || given.postcode === this.proposed.postcode) this.describe({ postcode: proposed.postcode });
    this.postcodesFound.set({ town: this.given().town, postcodes: town.postcodes });
    this.proposed = proposed;
  }

  protected typePostcode(postcode: string): void {
    this.describe({ postcode });
    if (!isPostcode(postcode)) return;
    const town = this.given().town;
    this.towns.ofPostcode(postcode).pipe(
      // The towns are only a help: without them, the administrator still types the town freely.
      catchError(() => of([])),
      // Only the towns of the postcode still given are of any help.
      filter(() => this.given().postcode === postcode),
      takeUntilDestroyed(this.destroyRef),
    ).subscribe(towns => {
      this.townsFound.set({ postcode, towns });
      if (towns.length === 1 && this.given().town === town) this.describe({ town: towns[0] });
    });
  }

  protected typeTown(town: string): void {
    this.describe({ town });
    if (!town.trim()) return;
    this.towns.postcodesOf(town).pipe(
      // The postcodes are only a help: without them, the administrator still types the postcode freely.
      catchError(() => of([])),
      // Only the postcodes of the town still given are of any help.
      filter(() => this.given().town === town),
      takeUntilDestroyed(this.destroyRef),
    ).subscribe(postcodes => {
      this.postcodesFound.set({ town, postcodes });
      if (postcodes.length === 1 && !this.given().postcode) this.describe({ postcode: postcodes[0] });
    });
  }

  protected describe(part: Partial<PostalAddress>): void {
    this.address().update(address => ({ ...address, ...part }));
  }
}
