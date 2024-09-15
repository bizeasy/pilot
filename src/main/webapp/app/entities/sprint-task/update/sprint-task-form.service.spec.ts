import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../sprint-task.test-samples';

import { SprintTaskFormService } from './sprint-task-form.service';

describe('SprintTask Form Service', () => {
  let service: SprintTaskFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SprintTaskFormService);
  });

  describe('Service methods', () => {
    describe('createSprintTaskFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createSprintTaskFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            sequenceNo: expect.any(Object),
            storyPoints: expect.any(Object),
            fromTime: expect.any(Object),
            thruTime: expect.any(Object),
            assignedTime: expect.any(Object),
            duration: expect.any(Object),
            task: expect.any(Object),
            sprint: expect.any(Object),
            assignedTo: expect.any(Object),
            assignedBy: expect.any(Object),
            qa: expect.any(Object),
            reviewedBy: expect.any(Object),
            status: expect.any(Object),
          }),
        );
      });

      it('passing ISprintTask should create a new form with FormGroup', () => {
        const formGroup = service.createSprintTaskFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            sequenceNo: expect.any(Object),
            storyPoints: expect.any(Object),
            fromTime: expect.any(Object),
            thruTime: expect.any(Object),
            assignedTime: expect.any(Object),
            duration: expect.any(Object),
            task: expect.any(Object),
            sprint: expect.any(Object),
            assignedTo: expect.any(Object),
            assignedBy: expect.any(Object),
            qa: expect.any(Object),
            reviewedBy: expect.any(Object),
            status: expect.any(Object),
          }),
        );
      });
    });

    describe('getSprintTask', () => {
      it('should return NewSprintTask for default SprintTask initial value', () => {
        const formGroup = service.createSprintTaskFormGroup(sampleWithNewData);

        const sprintTask = service.getSprintTask(formGroup) as any;

        expect(sprintTask).toMatchObject(sampleWithNewData);
      });

      it('should return NewSprintTask for empty SprintTask initial value', () => {
        const formGroup = service.createSprintTaskFormGroup();

        const sprintTask = service.getSprintTask(formGroup) as any;

        expect(sprintTask).toMatchObject({});
      });

      it('should return ISprintTask', () => {
        const formGroup = service.createSprintTaskFormGroup(sampleWithRequiredData);

        const sprintTask = service.getSprintTask(formGroup) as any;

        expect(sprintTask).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ISprintTask should not enable id FormControl', () => {
        const formGroup = service.createSprintTaskFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewSprintTask should disable id FormControl', () => {
        const formGroup = service.createSprintTaskFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
