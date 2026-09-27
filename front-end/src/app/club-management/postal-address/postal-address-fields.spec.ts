import { signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, TestRequest, provideHttpClientTesting } from '@angular/common/http/testing';
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
    fixture.componentRef.setInput('address', signal(NO_ADDRESS));
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

  function townsSought(postcode: string): TestRequest {
    return server.expectOne(request => request.url === '/api/towns' && request.params.get('postcode') === postcode);
  }

  it('keeps the town of the latest postcode when the towns of a previous one arrive late', async () => {
    fill(fieldLabelled(page, 'Code postal'), '45000');
    fill(fieldLabelled(page, 'Code postal'), '45160');

    townsSought('45160').flush(['OLIVET']);
    townsSought('45000').flush(['ORLEANS']);
    await fixture.whenStable();

    const town = fieldLabelled(page, 'Localité');
    expect(town.value).toBe('OLIVET');
    expect(suggestionsFor(page, town)).toEqual(['OLIVET']);
  });

  it('gives up the towns of a postcode as soon as another one is being typed', async () => {
    fill(fieldLabelled(page, 'Code postal'), '45000');
    fill(fieldLabelled(page, 'Code postal'), '4516');

    townsSought('45000').flush(['ORLEANS']);
    await fixture.whenStable();

    const town = fieldLabelled(page, 'Localité');
    expect(town.value).toBe('');
    expect(suggestionsFor(page, town)).toEqual([]);
  });

  it('no longer suggests the towns of a postcode once it is changed', async () => {
    await typePostcode('45240', ['LA FERTE ST AUBIN', 'LIGNY LE RIBAULT', 'SENNELY']);

    fill(fieldLabelled(page, 'Code postal'), '4524');
    await fixture.whenStable();

    expect(suggestionsFor(page, fieldLabelled(page, 'Localité'))).toEqual([]);
  });

  it('keeps suggesting the towns of the latest postcode when the search for a previous one fails', async () => {
    fill(fieldLabelled(page, 'Code postal'), '45000');
    fill(fieldLabelled(page, 'Code postal'), '45240');

    townsSought('45240').flush(['LA FERTE ST AUBIN', 'LIGNY LE RIBAULT', 'SENNELY']);
    townsSought('45000').flush(null, { status: 500, statusText: 'Internal Server Error' });
    await fixture.whenStable();

    expect(suggestionsFor(page, fieldLabelled(page, 'Localité'))).toEqual(['LA FERTE ST AUBIN', 'LIGNY LE RIBAULT', 'SENNELY']);
  });

  it('keeps the town the administrator typed while the towns of the postcode were being looked for', async () => {
    fill(fieldLabelled(page, 'Code postal'), '45000');
    await typeTown('Orléans', ['45000', '45100']);

    townsSought('45000').flush(['ORLEANS']);
    await fixture.whenStable();

    expect(fieldLabelled(page, 'Localité').value).toBe('Orléans');
  });

  function postcodesSought(town: string): TestRequest {
    return server.expectOne(request => request.url === '/api/postcodes' && request.params.get('town') === town);
  }

  it('suggests the postcodes of the latest town when those of a previous one arrive late', async () => {
    fill(fieldLabelled(page, 'Localité'), 'Orléans');
    fill(fieldLabelled(page, 'Localité'), 'Olivet');

    postcodesSought('Olivet').flush(['45160', '53410']);
    postcodesSought('Orléans').flush(['45000', '45100']);
    await fixture.whenStable();

    expect(suggestionsFor(page, fieldLabelled(page, 'Code postal'))).toEqual(['45160', '53410']);
  });

  it('gives up the postcodes of a town as soon as it is erased', async () => {
    fill(fieldLabelled(page, 'Localité'), 'Saint-Jean-de-Braye');
    fill(fieldLabelled(page, 'Localité'), '');

    postcodesSought('Saint-Jean-de-Braye').flush(['45800']);
    await fixture.whenStable();

    expect(fieldLabelled(page, 'Code postal').value).toBe('');
  });

  it('no longer suggests the postcodes of a town once it is changed', async () => {
    await typeTown('Orléans', ['45000', '45100']);

    fill(fieldLabelled(page, 'Localité'), '');
    await fixture.whenStable();

    expect(suggestionsFor(page, fieldLabelled(page, 'Code postal'))).toEqual([]);
  });

  it('keeps suggesting the postcodes of the latest town when the search for a previous one fails', async () => {
    fill(fieldLabelled(page, 'Localité'), 'Orléans');
    fill(fieldLabelled(page, 'Localité'), 'Olivet');

    postcodesSought('Olivet').flush(['45160', '53410']);
    postcodesSought('Orléans').flush(null, { status: 500, statusText: 'Internal Server Error' });
    await fixture.whenStable();

    expect(suggestionsFor(page, fieldLabelled(page, 'Code postal'))).toEqual(['45160', '53410']);
  });

  it('keeps the town typed meanwhile when the first search for a postcode typed again arrives', async () => {
    fill(fieldLabelled(page, 'Code postal'), '45000');
    fill(fieldLabelled(page, 'Code postal'), '45160');
    fill(fieldLabelled(page, 'Code postal'), '45000');
    fill(fieldLabelled(page, 'Localité'), 'Orléans');
    const [first, again] = server.match(request => request.url === '/api/towns' && request.params.get('postcode') === '45000');

    first.flush(['ORLEANS']);
    townsSought('45160').flush(['OLIVET']);
    again.flush(['ORLEANS']);
    await fixture.whenStable();

    const town = fieldLabelled(page, 'Localité');
    expect(town.value).toBe('Orléans');
    expect(suggestionsFor(page, town)).toEqual(['ORLEANS']);
    expect(fieldLabelled(page, 'Code postal').value).toBe('45000');
  });

  it('suggests no postcode of a town replaced by the town of the postcode in the meantime', async () => {
    fill(fieldLabelled(page, 'Localité'), 'Olivet');
    await typePostcode('45000', ['ORLEANS']);

    postcodesSought('Olivet').flush(['45160', '53410']);
    await fixture.whenStable();

    expect(fieldLabelled(page, 'Localité').value).toBe('ORLEANS');
    expect(suggestionsFor(page, fieldLabelled(page, 'Code postal'))).toEqual([]);
  });

  it('stops looking for the towns of the postcode once the address is no longer shown', () => {
    fill(fieldLabelled(page, 'Code postal'), '45000');

    fixture.destroy();

    expect(townsSought('45000').cancelled).toBe(true);
  });

  it('stops looking for the postcodes of the town once the address is no longer shown', () => {
    fill(fieldLabelled(page, 'Localité'), 'Orléans');

    fixture.destroy();

    expect(postcodesSought('Orléans').cancelled).toBe(true);
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
