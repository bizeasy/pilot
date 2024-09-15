import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../sprint-attachment.test-samples';

import { SprintAttachmentFormService } from './sprint-attachment-form.service';

describe('SprintAttachment Form Service', () => {
  let service: SprintAttachmentFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SprintAttachmentFormService);
  });

  describe('Service methods', () => {
    describe('createSprintAttachmentFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createSprintAttachmentFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            sprint: expect.any(Object),
            attachment: expect.any(Object),
          }),
        );
      });

      it('passing ISprintAttachment should create a new form with FormGroup', () => {
        const formGroup = service.createSprintAttachmentFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            sprint: expect.any(Object),
            attachment: expect.any(Object),
          }),
        );
      });
    });

    describe('getSprintAttachment', () => {
      it('should return NewSprintAttachment for default SprintAttachment initial value', () => {
        const formGroup = service.createSprintAttachmentFormGroup(sampleWithNewData);

        const sprintAttachment = service.getSprintAttachment(formGroup) as any;

        expect(sprintAttachment).toMatchObject(sampleWithNewData);
      });

      it('should return NewSprintAttachment for empty SprintAttachment initial value', () => {
        const formGroup = service.createSprintAttachmentFormGroup();

        const sprintAttachment = service.getSprintAttachment(formGroup) as any;

        expect(sprintAttachment).toMatchObject({});
      });

      it('should return ISprintAttachment', () => {
        const formGroup = service.createSprintAttachmentFormGroup(sampleWithRequiredData);

        const sprintAttachment = service.getSprintAttachment(formGroup) as any;

        expect(sprintAttachment).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ISprintAttachment should not enable id FormControl', () => {
        const formGroup = service.createSprintAttachmentFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewSprintAttachment should disable id FormControl', () => {
        const formGroup = service.createSprintAttachmentFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
