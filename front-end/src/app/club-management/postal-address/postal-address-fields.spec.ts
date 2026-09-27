import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { PostalAddressFields } from './postal-address-fields';
import { NO_ADDRESS } from './postal-address';
import { Towns } from '../ports/towns';
import { HttpTowns } from '../http/http-towns';

describe('PostalAddressFields', () => {
  let fixture: ComponentFixture<PostalAddressFields>;
  let server: HttpTestingController;
  let page: HTMLElement;

  beforeEach(async () => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting(), { provide: Towns, useClass: HttpTowns }],
    });
    fixture = TestBed.createComponent(PostalAddressFields);
    fixture.componentRef.setInput('idPrefix', 'office');
    fixture.componentRef.setInput('address', NO_ADDRESS);
    server = TestBed.inject(HttpTestingController);
    page = fixture.nativeElement as HTMLElement;
    await fixture.whenStable();
  });

  async function typePostcode(postcode: string, towns: string[]): Promise<void> {
    fill(fieldLabelled(page, 'Code postal'), postcode);
    server.expectOne(request => request.method === 'GET' && request.url === '/towns'
      && request.params.get('postcode') === postcode).flush(towns);
    await fixture.whenStable();
  }

  it('fills in the town when the postcode serves only one', async () => {
    await typePostcode('45000', ['ORLEANS']);

    expect(fieldLabelled(page, 'Localité').value).toBe('ORLEANS');
  });

  it('suggests the towns without choosing one when the postcode serves several', async () => {
    const laFerteStAubin = ['LA FERTE ST AUBIN', 'LIGNY LE RIBAULT', 'MARCILLY EN VILLETTE', 'MENESTREAU EN VILLETTE', 'SENNELY'];

    await typePostcode('45240', laFerteStAubin);

    const town = fieldLabelled(page, 'Localité');
    expect(town.value).toBe('');
    expect(suggestionsFor(page, town)).toEqual(laFerteStAubin);
  });

  it('does not look for the towns until the postcode is complete', async () => {
    fill(fieldLabelled(page, 'Code postal'), '450');
    await fixture.whenStable();

    server.expectNone(request => request.url === '/towns');
  });
});

function fill(field: HTMLInputElement, value: string): void {
  field.value = value;
  field.dispatchEvent(new Event('input'));
}

function suggestionsFor(page: HTMLElement, field: HTMLInputElement): string[] {
  const list = field.getAttribute('list');
  if (!list) return [];
  return Array.from(page.querySelectorAll<HTMLOptionElement>(`datalist[id="${list}"] option`)).map(option => option.value);
}

function fieldLabelled(page: HTMLElement, text: string): HTMLInputElement {
  const label = Array.from(page.querySelectorAll('label')).find(l => l.textContent?.trim() === text);
  if (!label?.control) throw new Error(`No field labelled "${text}"`);
  return label.control as HTMLInputElement;
}
