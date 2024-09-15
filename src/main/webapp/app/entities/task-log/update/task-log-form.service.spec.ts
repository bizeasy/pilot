import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../task-log.test-samples';

import { TaskLogFormService } from './task-log-form.service';

describe('TaskLog Form Service', () => {
  let service: TaskLogFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TaskLogFormService);
  });

  describe('Service methods', () => {
    describe('createTaskLogFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTaskLogFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            comments: expect.any(Object),
            fromTime: expect.any(Object),
            toTime: expect.any(Object),
            task: expect.any(Object),
          }),
        );
      });

      it('passing ITaskLog should create a new form with FormGroup', () => {
        const formGroup = service.createTaskLogFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            comments: expect.any(Object),
            fromTime: expect.any(Object),
            toTime: expect.any(Object),
            task: expect.any(Object),
          }),
        );
      });
    });

    describe('getTaskLog', () => {
      it('should return NewTaskLog for default TaskLog initial value', () => {
        const formGroup = service.createTaskLogFormGroup(sampleWithNewData);

        const taskLog = service.getTaskLog(formGroup) as any;

        expect(taskLog).toMatchObject(sampleWithNewData);
      });

      it('should return NewTaskLog for empty TaskLog initial value', () => {
        const formGroup = service.createTaskLogFormGroup();

        const taskLog = service.getTaskLog(formGroup) as any;

        expect(taskLog).toMatchObject({});
      });

      it('should return ITaskLog', () => {
        const formGroup = service.createTaskLogFormGroup(sampleWithRequiredData);

        const taskLog = service.getTaskLog(formGroup) as any;

        expect(taskLog).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITaskLog should not enable id FormControl', () => {
        const formGroup = service.createTaskLogFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewTaskLog should disable id FormControl', () => {
        const formGroup = service.createTaskLogFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
