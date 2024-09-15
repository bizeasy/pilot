import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../party.test-samples';

import { PartyFormService } from './party-form.service';

describe('Party Form Service', () => {
  let service: PartyFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PartyFormService);
  });

  describe('Service methods', () => {
    describe('createPartyFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createPartyFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            firstName: expect.any(Object),
            lastName: expect.any(Object),
            displayName: expect.any(Object),
            email: expect.any(Object),
            dob: expect.any(Object),
            notes: expect.any(Object),
            mobileNumber: expect.any(Object),
            employeeId: expect.any(Object),
            login: expect.any(Object),
            user: expect.any(Object),
            status: expect.any(Object),
            partyType: expect.any(Object),
          }),
        );
      });

      it('passing IParty should create a new form with FormGroup', () => {
        const formGroup = service.createPartyFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            firstName: expect.any(Object),
            lastName: expect.any(Object),
            displayName: expect.any(Object),
            email: expect.any(Object),
            dob: expect.any(Object),
            notes: expect.any(Object),
            mobileNumber: expect.any(Object),
            employeeId: expect.any(Object),
            login: expect.any(Object),
            user: expect.any(Object),
            status: expect.any(Object),
            partyType: expect.any(Object),
          }),
        );
      });
    });

    describe('getParty', () => {
      it('should return NewParty for default Party initial value', () => {
        const formGroup = service.createPartyFormGroup(sampleWithNewData);

        const party = service.getParty(formGroup) as any;

        expect(party).toMatchObject(sampleWithNewData);
      });

      it('should return NewParty for empty Party initial value', () => {
        const formGroup = service.createPartyFormGroup();

        const party = service.getParty(formGroup) as any;

        expect(party).toMatchObject({});
      });

      it('should return IParty', () => {
        const formGroup = service.createPartyFormGroup(sampleWithRequiredData);

        const party = service.getParty(formGroup) as any;

        expect(party).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IParty should not enable id FormControl', () => {
        const formGroup = service.createPartyFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewParty should disable id FormControl', () => {
        const formGroup = service.createPartyFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
