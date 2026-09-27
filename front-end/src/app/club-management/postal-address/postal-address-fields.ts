import { Component, inject, input, model, signal } from '@angular/core';
import { AddressProblem, PostalAddress, isPostcode } from './postal-address';
import { Towns } from '../ports/towns';

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
        [value]="address().street"
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
        [value]="address().postcode"
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
        [value]="address().town"
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
  readonly address = model.required<PostalAddress>();
  readonly problems = input<ReadonlySet<AddressProblem>>(new Set());

  private readonly towns = inject(Towns);
  protected readonly servedTowns = signal<readonly string[]>([]);
  protected readonly servingPostcodes = signal<readonly string[]>([]);

  protected typePostcode(postcode: string): void {
    this.describe({ postcode });
    if (!isPostcode(postcode)) return;
    this.towns.ofPostcode(postcode).subscribe({
      next: towns => {
        this.servedTowns.set(towns);
        if (towns.length === 1) this.describe({ town: towns[0] });
      },
      // The towns are only a help: without them, the administrator still types the town freely.
      error: () => this.servedTowns.set([]),
    });
  }

  protected typeTown(town: string): void {
    this.describe({ town });
    if (!town.trim()) return;
    this.towns.postcodesOf(town).subscribe({
      next: postcodes => {
        this.servingPostcodes.set(postcodes);
        if (postcodes.length === 1 && !this.address().postcode) this.describe({ postcode: postcodes[0] });
      },
      // The postcodes are only a help: without them, the administrator still types the postcode freely.
      error: () => this.servingPostcodes.set([]),
    });
  }

  protected describe(part: Partial<PostalAddress>): void {
    this.address.update(address => ({ ...address, ...part }));
  }
}
