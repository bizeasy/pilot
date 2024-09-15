import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ISprint, NewSprint } from '../sprint.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ISprint for edit and NewSprintFormGroupInput for create.
 */
type SprintFormGroupInput = ISprint | PartialWithRequiredKeyOf<NewSprint>;

type SprintFormDefaults = Pick<NewSprint, 'id'>;

type SprintFormGroupContent = {
  id: FormControl<ISprint['id'] | NewSprint['id']>;
  name: FormControl<ISprint['name']>;
  startDate: FormControl<ISprint['startDate']>;
  endDate: FormControl<ISprint['endDate']>;
  goal: FormControl<ISprint['goal']>;
  totalPoints: FormControl<ISprint['totalPoints']>;
  project: FormControl<ISprint['project']>;
  status: FormControl<ISprint['status']>;
};

export type SprintFormGroup = FormGroup<SprintFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class SprintFormService {
  createSprintFormGroup(sprint: SprintFormGroupInput = { id: null }): SprintFormGroup {
    const sprintRawValue = {
      ...this.getFormDefaults(),
      ...sprint,
    };
    return new FormGroup<SprintFormGroupContent>({
      id: new FormControl(
        { value: sprintRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(sprintRawValue.name, {
        validators: [Validators.required],
      }),
      startDate: new FormControl(sprintRawValue.startDate, {
        validators: [Validators.required],
      }),
      endDate: new FormControl(sprintRawValue.endDate, {
        validators: [Validators.required],
      }),
      goal: new FormControl(sprintRawValue.goal),
      totalPoints: new FormControl(sprintRawValue.totalPoints),
      project: new FormControl(sprintRawValue.project),
      status: new FormControl(sprintRawValue.status),
    });
  }

  getSprint(form: SprintFormGroup): ISprint | NewSprint {
    return form.getRawValue() as ISprint | NewSprint;
  }

  resetForm(form: SprintFormGroup, sprint: SprintFormGroupInput): void {
    const sprintRawValue = { ...this.getFormDefaults(), ...sprint };
    form.reset(
      {
        ...sprintRawValue,
        id: { value: sprintRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): SprintFormDefaults {
    return {
      id: null,
    };
  }
}
