import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../task-history.test-samples';

import { TaskHistoryFormService } from './task-history-form.service';

describe('TaskHistory Form Service', () => {
  let service: TaskHistoryFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TaskHistoryFormService);
  });

  describe('Service methods', () => {
    describe('createTaskHistoryFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTaskHistoryFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            type: expect.any(Object),
            assignedTo: expect.any(Object),
            sprint: expect.any(Object),
            assignedBy: expect.any(Object),
          }),
        );
      });

      it('passing ITaskHistory should create a new form with FormGroup', () => {
        const formGroup = service.createTaskHistoryFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            type: expect.any(Object),
            assignedTo: expect.any(Object),
            sprint: expect.any(Object),
            assignedBy: expect.any(Object),
          }),
        );
      });
    });

    describe('getTaskHistory', () => {
      it('should return NewTaskHistory for default TaskHistory initial value', () => {
        const formGroup = service.createTaskHistoryFormGroup(sampleWithNewData);

        const taskHistory = service.getTaskHistory(formGroup) as any;

        expect(taskHistory).toMatchObject(sampleWithNewData);
      });

      it('should return NewTaskHistory for empty TaskHistory initial value', () => {
        const formGroup = service.createTaskHistoryFormGroup();

        const taskHistory = service.getTaskHistory(formGroup) as any;

        expect(taskHistory).toMatchObject({});
      });

      it('should return ITaskHistory', () => {
        const formGroup = service.createTaskHistoryFormGroup(sampleWithRequiredData);

        const taskHistory = service.getTaskHistory(formGroup) as any;

        expect(taskHistory).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITaskHistory should not enable id FormControl', () => {
        const formGroup = service.createTaskHistoryFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewTaskHistory should disable id FormControl', () => {
        const formGroup = service.createTaskHistoryFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
