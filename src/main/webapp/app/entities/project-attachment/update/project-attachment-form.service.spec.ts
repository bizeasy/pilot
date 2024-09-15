import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../project-attachment.test-samples';

import { ProjectAttachmentFormService } from './project-attachment-form.service';

describe('ProjectAttachment Form Service', () => {
  let service: ProjectAttachmentFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ProjectAttachmentFormService);
  });

  describe('Service methods', () => {
    describe('createProjectAttachmentFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createProjectAttachmentFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            facility: expect.any(Object),
            attachment: expect.any(Object),
          }),
        );
      });

      it('passing IProjectAttachment should create a new form with FormGroup', () => {
        const formGroup = service.createProjectAttachmentFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            facility: expect.any(Object),
            attachment: expect.any(Object),
          }),
        );
      });
    });

    describe('getProjectAttachment', () => {
      it('should return NewProjectAttachment for default ProjectAttachment initial value', () => {
        const formGroup = service.createProjectAttachmentFormGroup(sampleWithNewData);

        const projectAttachment = service.getProjectAttachment(formGroup) as any;

        expect(projectAttachment).toMatchObject(sampleWithNewData);
      });

      it('should return NewProjectAttachment for empty ProjectAttachment initial value', () => {
        const formGroup = service.createProjectAttachmentFormGroup();

        const projectAttachment = service.getProjectAttachment(formGroup) as any;

        expect(projectAttachment).toMatchObject({});
      });

      it('should return IProjectAttachment', () => {
        const formGroup = service.createProjectAttachmentFormGroup(sampleWithRequiredData);

        const projectAttachment = service.getProjectAttachment(formGroup) as any;

        expect(projectAttachment).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IProjectAttachment should not enable id FormControl', () => {
        const formGroup = service.createProjectAttachmentFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewProjectAttachment should disable id FormControl', () => {
        const formGroup = service.createProjectAttachmentFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
