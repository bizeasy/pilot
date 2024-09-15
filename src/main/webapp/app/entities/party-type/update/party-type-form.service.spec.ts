import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../party-type.test-samples';

import { PartyTypeFormService } from './party-type-form.service';

describe('PartyType Form Service', () => {
  let service: PartyTypeFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PartyTypeFormService);
  });

  describe('Service methods', () => {
    describe('createPartyTypeFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createPartyTypeFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
          }),
        );
      });

      it('passing IPartyType should create a new form with FormGroup', () => {
        const formGroup = service.createPartyTypeFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
          }),
        );
      });
    });

    describe('getPartyType', () => {
      it('should return NewPartyType for default PartyType initial value', () => {
        const formGroup = service.createPartyTypeFormGroup(sampleWithNewData);

        const partyType = service.getPartyType(formGroup) as any;

        expect(partyType).toMatchObject(sampleWithNewData);
      });

      it('should return NewPartyType for empty PartyType initial value', () => {
        const formGroup = service.createPartyTypeFormGroup();

        const partyType = service.getPartyType(formGroup) as any;

        expect(partyType).toMatchObject({});
      });

      it('should return IPartyType', () => {
        const formGroup = service.createPartyTypeFormGroup(sampleWithRequiredData);

        const partyType = service.getPartyType(formGroup) as any;

        expect(partyType).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IPartyType should not enable id FormControl', () => {
        const formGroup = service.createPartyTypeFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewPartyType should disable id FormControl', () => {
        const formGroup = service.createPartyTypeFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
