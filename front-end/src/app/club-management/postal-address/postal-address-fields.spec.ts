import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { PostalAddressFields } from './postal-address-fields';
import { NO_ADDRESS } from './postal-address';
import { DeliveryTown, Towns } from '../ports/towns';
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
    server.expectOne(request => request.method === 'GET' && request.url === '/api/towns'
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

  it('suggests no town and leaves the town free when the towns cannot be found', async () => {
    fill(fieldLabelled(page, 'Code postal'), '45240');
    server.expectOne(request => request.url === '/api/towns')
      .flush(null, { status: 500, statusText: 'Internal Server Error' });
    await fixture.whenStable();

    const town = fieldLabelled(page, 'Localité');
    expect(town.value).toBe('');
    expect(suggestionsFor(page, town)).toEqual([]);
  });

  async function typeTown(town: string, postcodes: string[]): Promise<void> {
    fill(fieldLabelled(page, 'Localité'), town);
    server.expectOne(request => request.method === 'GET' && request.url === '/api/postcodes'
      && request.params.get('town') === town).flush(postcodes);
    await fixture.whenStable();
  }

  it('fills in the postcode when the town has only one and none is given yet', async () => {
    await typeTown('Saint-Jean-de-Braye', ['45800']);

    expect(fieldLabelled(page, 'Code postal').value).toBe('45800');
  });

  it('suggests the postcodes without choosing one when the town has several', async () => {
    await typeTown('Orléans', ['45000', '45100']);

    const postcode = fieldLabelled(page, 'Code postal');
    expect(postcode.value).toBe('');
    expect(suggestionsFor(page, postcode)).toEqual(['45000', '45100']);
  });

  it('keeps the postcode already given even when the town has only one', async () => {
    await typePostcode('45100', ['ORLEANS']);

    await typeTown('Saint-Jean-de-Braye', ['45800']);

    expect(fieldLabelled(page, 'Code postal').value).toBe('45100');
  });

  it('suggests no postcode and leaves the postcode free when the postcodes cannot be found', async () => {
    fill(fieldLabelled(page, 'Localité'), 'Orléans');
    server.expectOne(request => request.url === '/api/postcodes')
      .flush(null, { status: 500, statusText: 'Internal Server Error' });
    await fixture.whenStable();

    const postcode = fieldLabelled(page, 'Code postal');
    expect(postcode.value).toBe('');
    expect(suggestionsFor(page, postcode)).toEqual([]);
  });

  it('does not look for the postcodes of a blank town', async () => {
    fill(fieldLabelled(page, 'Localité'), '  ');
    await fixture.whenStable();

    server.expectNone(request => request.url === '/api/postcodes');
  });

  async function defaultTo(town: DeliveryTown): Promise<void> {
    fixture.componentRef.setInput('defaultTown', town);
    await fixture.whenStable();
  }

  it('takes the town of the commune, with its postcode, when nothing is given yet', async () => {
    await defaultTo({ name: 'OLIVET', postcodes: ['45160'] });

    expect(fieldLabelled(page, 'Localité').value).toBe('OLIVET');
    expect(fieldLabelled(page, 'Code postal').value).toBe('45160');
  });

  it('suggests the postcodes of the town of the commune without choosing one when it has several', async () => {
    await defaultTo({ name: 'ORLEANS', postcodes: ['45000', '45100'] });

    const postcode = fieldLabelled(page, 'Code postal');
    expect(fieldLabelled(page, 'Localité').value).toBe('ORLEANS');
    expect(postcode.value).toBe('');
    expect(suggestionsFor(page, postcode)).toEqual(['45000', '45100']);
  });

  it('keeps the town and the postcode the administrator gave', async () => {
    await typePostcode('45100', ['ORLEANS']);

    await defaultTo({ name: 'OLIVET', postcodes: ['45160'] });

    expect(fieldLabelled(page, 'Localité').value).toBe('ORLEANS');
    expect(fieldLabelled(page, 'Code postal').value).toBe('45100');
  });

  it('replaces the town and the postcode of the commune chosen before', async () => {
    await defaultTo({ name: 'OLIVET', postcodes: ['45160'] });

    await defaultTo({ name: 'COLMARS LES ALPES', postcodes: ['04370'] });

    expect(fieldLabelled(page, 'Localité').value).toBe('COLMARS LES ALPES');
    expect(fieldLabelled(page, 'Code postal').value).toBe('04370');
  });

  it('does not look for the towns until the postcode is complete', async () => {
    fill(fieldLabelled(page, 'Code postal'), '450');
    await fixture.whenStable();

    server.expectNone(request => request.url === '/api/towns');
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
