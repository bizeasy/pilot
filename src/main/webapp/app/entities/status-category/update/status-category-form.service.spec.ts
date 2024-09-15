import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../status-category.test-samples';

import { StatusCategoryFormService } from './status-category-form.service';

describe('StatusCategory Form Service', () => {
  let service: StatusCategoryFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(StatusCategoryFormService);
  });

  describe('Service methods', () => {
    describe('createStatusCategoryFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createStatusCategoryFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
          }),
        );
      });

      it('passing IStatusCategory should create a new form with FormGroup', () => {
        const formGroup = service.createStatusCategoryFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
          }),
        );
      });
    });

    describe('getStatusCategory', () => {
      it('should return NewStatusCategory for default StatusCategory initial value', () => {
        const formGroup = service.createStatusCategoryFormGroup(sampleWithNewData);

        const statusCategory = service.getStatusCategory(formGroup) as any;

        expect(statusCategory).toMatchObject(sampleWithNewData);
      });

      it('should return NewStatusCategory for empty StatusCategory initial value', () => {
        const formGroup = service.createStatusCategoryFormGroup();

        const statusCategory = service.getStatusCategory(formGroup) as any;

        expect(statusCategory).toMatchObject({});
      });

      it('should return IStatusCategory', () => {
        const formGroup = service.createStatusCategoryFormGroup(sampleWithRequiredData);

        const statusCategory = service.getStatusCategory(formGroup) as any;

        expect(statusCategory).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IStatusCategory should not enable id FormControl', () => {
        const formGroup = service.createStatusCategoryFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewStatusCategory should disable id FormControl', () => {
        const formGroup = service.createStatusCategoryFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
