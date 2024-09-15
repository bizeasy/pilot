import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../task-attachment.test-samples';

import { TaskAttachmentFormService } from './task-attachment-form.service';

describe('TaskAttachment Form Service', () => {
  let service: TaskAttachmentFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TaskAttachmentFormService);
  });

  describe('Service methods', () => {
    describe('createTaskAttachmentFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTaskAttachmentFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            task: expect.any(Object),
            attachment: expect.any(Object),
          }),
        );
      });

      it('passing ITaskAttachment should create a new form with FormGroup', () => {
        const formGroup = service.createTaskAttachmentFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            task: expect.any(Object),
            attachment: expect.any(Object),
          }),
        );
      });
    });

    describe('getTaskAttachment', () => {
      it('should return NewTaskAttachment for default TaskAttachment initial value', () => {
        const formGroup = service.createTaskAttachmentFormGroup(sampleWithNewData);

        const taskAttachment = service.getTaskAttachment(formGroup) as any;

        expect(taskAttachment).toMatchObject(sampleWithNewData);
      });

      it('should return NewTaskAttachment for empty TaskAttachment initial value', () => {
        const formGroup = service.createTaskAttachmentFormGroup();

        const taskAttachment = service.getTaskAttachment(formGroup) as any;

        expect(taskAttachment).toMatchObject({});
      });

      it('should return ITaskAttachment', () => {
        const formGroup = service.createTaskAttachmentFormGroup(sampleWithRequiredData);

        const taskAttachment = service.getTaskAttachment(formGroup) as any;

        expect(taskAttachment).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITaskAttachment should not enable id FormControl', () => {
        const formGroup = service.createTaskAttachmentFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewTaskAttachment should disable id FormControl', () => {
        const formGroup = service.createTaskAttachmentFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
