import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../sprint-task-history.test-samples';

import { SprintTaskHistoryFormService } from './sprint-task-history-form.service';

describe('SprintTaskHistory Form Service', () => {
  let service: SprintTaskHistoryFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SprintTaskHistoryFormService);
  });

  describe('Service methods', () => {
    describe('createSprintTaskHistoryFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createSprintTaskHistoryFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            comments: expect.any(Object),
            fromDate: expect.any(Object),
            toDate: expect.any(Object),
            fromStatus: expect.any(Object),
            toStatus: expect.any(Object),
          }),
        );
      });

      it('passing ISprintTaskHistory should create a new form with FormGroup', () => {
        const formGroup = service.createSprintTaskHistoryFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            comments: expect.any(Object),
            fromDate: expect.any(Object),
            toDate: expect.any(Object),
            fromStatus: expect.any(Object),
            toStatus: expect.any(Object),
          }),
        );
      });
    });

    describe('getSprintTaskHistory', () => {
      it('should return NewSprintTaskHistory for default SprintTaskHistory initial value', () => {
        const formGroup = service.createSprintTaskHistoryFormGroup(sampleWithNewData);

        const sprintTaskHistory = service.getSprintTaskHistory(formGroup) as any;

        expect(sprintTaskHistory).toMatchObject(sampleWithNewData);
      });

      it('should return NewSprintTaskHistory for empty SprintTaskHistory initial value', () => {
        const formGroup = service.createSprintTaskHistoryFormGroup();

        const sprintTaskHistory = service.getSprintTaskHistory(formGroup) as any;

        expect(sprintTaskHistory).toMatchObject({});
      });

      it('should return ISprintTaskHistory', () => {
        const formGroup = service.createSprintTaskHistoryFormGroup(sampleWithRequiredData);

        const sprintTaskHistory = service.getSprintTaskHistory(formGroup) as any;

        expect(sprintTaskHistory).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ISprintTaskHistory should not enable id FormControl', () => {
        const formGroup = service.createSprintTaskHistoryFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewSprintTaskHistory should disable id FormControl', () => {
        const formGroup = service.createSprintTaskHistoryFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
