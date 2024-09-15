import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../individual-performance.test-samples';

import { IndividualPerformanceFormService } from './individual-performance-form.service';

describe('IndividualPerformance Form Service', () => {
  let service: IndividualPerformanceFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(IndividualPerformanceFormService);
  });

  describe('Service methods', () => {
    describe('createIndividualPerformanceFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createIndividualPerformanceFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            completedTasks: expect.any(Object),
            velocity: expect.any(Object),
            storyPointsCompleted: expect.any(Object),
            party: expect.any(Object),
            sprint: expect.any(Object),
          }),
        );
      });

      it('passing IIndividualPerformance should create a new form with FormGroup', () => {
        const formGroup = service.createIndividualPerformanceFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            completedTasks: expect.any(Object),
            velocity: expect.any(Object),
            storyPointsCompleted: expect.any(Object),
            party: expect.any(Object),
            sprint: expect.any(Object),
          }),
        );
      });
    });

    describe('getIndividualPerformance', () => {
      it('should return NewIndividualPerformance for default IndividualPerformance initial value', () => {
        const formGroup = service.createIndividualPerformanceFormGroup(sampleWithNewData);

        const individualPerformance = service.getIndividualPerformance(formGroup) as any;

        expect(individualPerformance).toMatchObject(sampleWithNewData);
      });

      it('should return NewIndividualPerformance for empty IndividualPerformance initial value', () => {
        const formGroup = service.createIndividualPerformanceFormGroup();

        const individualPerformance = service.getIndividualPerformance(formGroup) as any;

        expect(individualPerformance).toMatchObject({});
      });

      it('should return IIndividualPerformance', () => {
        const formGroup = service.createIndividualPerformanceFormGroup(sampleWithRequiredData);

        const individualPerformance = service.getIndividualPerformance(formGroup) as any;

        expect(individualPerformance).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IIndividualPerformance should not enable id FormControl', () => {
        const formGroup = service.createIndividualPerformanceFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewIndividualPerformance should disable id FormControl', () => {
        const formGroup = service.createIndividualPerformanceFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
