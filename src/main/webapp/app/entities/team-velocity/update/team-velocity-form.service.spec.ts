import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../team-velocity.test-samples';

import { TeamVelocityFormService } from './team-velocity-form.service';

describe('TeamVelocity Form Service', () => {
  let service: TeamVelocityFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TeamVelocityFormService);
  });

  describe('Service methods', () => {
    describe('createTeamVelocityFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTeamVelocityFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            sprintVelocity: expect.any(Object),
            averageVelocity: expect.any(Object),
            sprint: expect.any(Object),
          }),
        );
      });

      it('passing ITeamVelocity should create a new form with FormGroup', () => {
        const formGroup = service.createTeamVelocityFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            sprintVelocity: expect.any(Object),
            averageVelocity: expect.any(Object),
            sprint: expect.any(Object),
          }),
        );
      });
    });

    describe('getTeamVelocity', () => {
      it('should return NewTeamVelocity for default TeamVelocity initial value', () => {
        const formGroup = service.createTeamVelocityFormGroup(sampleWithNewData);

        const teamVelocity = service.getTeamVelocity(formGroup) as any;

        expect(teamVelocity).toMatchObject(sampleWithNewData);
      });

      it('should return NewTeamVelocity for empty TeamVelocity initial value', () => {
        const formGroup = service.createTeamVelocityFormGroup();

        const teamVelocity = service.getTeamVelocity(formGroup) as any;

        expect(teamVelocity).toMatchObject({});
      });

      it('should return ITeamVelocity', () => {
        const formGroup = service.createTeamVelocityFormGroup(sampleWithRequiredData);

        const teamVelocity = service.getTeamVelocity(formGroup) as any;

        expect(teamVelocity).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITeamVelocity should not enable id FormControl', () => {
        const formGroup = service.createTeamVelocityFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewTeamVelocity should disable id FormControl', () => {
        const formGroup = service.createTeamVelocityFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
