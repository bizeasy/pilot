import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ITeamVelocity, NewTeamVelocity } from '../team-velocity.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITeamVelocity for edit and NewTeamVelocityFormGroupInput for create.
 */
type TeamVelocityFormGroupInput = ITeamVelocity | PartialWithRequiredKeyOf<NewTeamVelocity>;

type TeamVelocityFormDefaults = Pick<NewTeamVelocity, 'id'>;

type TeamVelocityFormGroupContent = {
  id: FormControl<ITeamVelocity['id'] | NewTeamVelocity['id']>;
  sprintVelocity: FormControl<ITeamVelocity['sprintVelocity']>;
  averageVelocity: FormControl<ITeamVelocity['averageVelocity']>;
  sprint: FormControl<ITeamVelocity['sprint']>;
};

export type TeamVelocityFormGroup = FormGroup<TeamVelocityFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TeamVelocityFormService {
  createTeamVelocityFormGroup(teamVelocity: TeamVelocityFormGroupInput = { id: null }): TeamVelocityFormGroup {
    const teamVelocityRawValue = {
      ...this.getFormDefaults(),
      ...teamVelocity,
    };
    return new FormGroup<TeamVelocityFormGroupContent>({
      id: new FormControl(
        { value: teamVelocityRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      sprintVelocity: new FormControl(teamVelocityRawValue.sprintVelocity, {
        validators: [Validators.required],
      }),
      averageVelocity: new FormControl(teamVelocityRawValue.averageVelocity, {
        validators: [Validators.required],
      }),
      sprint: new FormControl(teamVelocityRawValue.sprint),
    });
  }

  getTeamVelocity(form: TeamVelocityFormGroup): ITeamVelocity | NewTeamVelocity {
    return form.getRawValue() as ITeamVelocity | NewTeamVelocity;
  }

  resetForm(form: TeamVelocityFormGroup, teamVelocity: TeamVelocityFormGroupInput): void {
    const teamVelocityRawValue = { ...this.getFormDefaults(), ...teamVelocity };
    form.reset(
      {
        ...teamVelocityRawValue,
        id: { value: teamVelocityRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): TeamVelocityFormDefaults {
    return {
      id: null,
    };
  }
}
