import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../retrospective.test-samples';

import { RetrospectiveFormService } from './retrospective-form.service';

describe('Retrospective Form Service', () => {
  let service: RetrospectiveFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RetrospectiveFormService);
  });

  describe('Service methods', () => {
    describe('createRetrospectiveFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createRetrospectiveFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            summary: expect.any(Object),
            actionItems: expect.any(Object),
            dateCreated: expect.any(Object),
            sprint: expect.any(Object),
          }),
        );
      });

      it('passing IRetrospective should create a new form with FormGroup', () => {
        const formGroup = service.createRetrospectiveFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            summary: expect.any(Object),
            actionItems: expect.any(Object),
            dateCreated: expect.any(Object),
            sprint: expect.any(Object),
          }),
        );
      });
    });

    describe('getRetrospective', () => {
      it('should return NewRetrospective for default Retrospective initial value', () => {
        const formGroup = service.createRetrospectiveFormGroup(sampleWithNewData);

        const retrospective = service.getRetrospective(formGroup) as any;

        expect(retrospective).toMatchObject(sampleWithNewData);
      });

      it('should return NewRetrospective for empty Retrospective initial value', () => {
        const formGroup = service.createRetrospectiveFormGroup();

        const retrospective = service.getRetrospective(formGroup) as any;

        expect(retrospective).toMatchObject({});
      });

      it('should return IRetrospective', () => {
        const formGroup = service.createRetrospectiveFormGroup(sampleWithRequiredData);

        const retrospective = service.getRetrospective(formGroup) as any;

        expect(retrospective).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IRetrospective should not enable id FormControl', () => {
        const formGroup = service.createRetrospectiveFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewRetrospective should disable id FormControl', () => {
        const formGroup = service.createRetrospectiveFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
