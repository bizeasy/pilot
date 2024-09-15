import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../party-attachment.test-samples';

import { PartyAttachmentFormService } from './party-attachment-form.service';

describe('PartyAttachment Form Service', () => {
  let service: PartyAttachmentFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PartyAttachmentFormService);
  });

  describe('Service methods', () => {
    describe('createPartyAttachmentFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createPartyAttachmentFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            party: expect.any(Object),
            attachment: expect.any(Object),
          }),
        );
      });

      it('passing IPartyAttachment should create a new form with FormGroup', () => {
        const formGroup = service.createPartyAttachmentFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            party: expect.any(Object),
            attachment: expect.any(Object),
          }),
        );
      });
    });

    describe('getPartyAttachment', () => {
      it('should return NewPartyAttachment for default PartyAttachment initial value', () => {
        const formGroup = service.createPartyAttachmentFormGroup(sampleWithNewData);

        const partyAttachment = service.getPartyAttachment(formGroup) as any;

        expect(partyAttachment).toMatchObject(sampleWithNewData);
      });

      it('should return NewPartyAttachment for empty PartyAttachment initial value', () => {
        const formGroup = service.createPartyAttachmentFormGroup();

        const partyAttachment = service.getPartyAttachment(formGroup) as any;

        expect(partyAttachment).toMatchObject({});
      });

      it('should return IPartyAttachment', () => {
        const formGroup = service.createPartyAttachmentFormGroup(sampleWithRequiredData);

        const partyAttachment = service.getPartyAttachment(formGroup) as any;

        expect(partyAttachment).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IPartyAttachment should not enable id FormControl', () => {
        const formGroup = service.createPartyAttachmentFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewPartyAttachment should disable id FormControl', () => {
        const formGroup = service.createPartyAttachmentFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
