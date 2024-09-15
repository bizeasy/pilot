import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ISprintTaskHistory, NewSprintTaskHistory } from '../sprint-task-history.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ISprintTaskHistory for edit and NewSprintTaskHistoryFormGroupInput for create.
 */
type SprintTaskHistoryFormGroupInput = ISprintTaskHistory | PartialWithRequiredKeyOf<NewSprintTaskHistory>;

type SprintTaskHistoryFormDefaults = Pick<NewSprintTaskHistory, 'id'>;

type SprintTaskHistoryFormGroupContent = {
  id: FormControl<ISprintTaskHistory['id'] | NewSprintTaskHistory['id']>;
  comments: FormControl<ISprintTaskHistory['comments']>;
  fromDate: FormControl<ISprintTaskHistory['fromDate']>;
  toDate: FormControl<ISprintTaskHistory['toDate']>;
  fromStatus: FormControl<ISprintTaskHistory['fromStatus']>;
  toStatus: FormControl<ISprintTaskHistory['toStatus']>;
};

export type SprintTaskHistoryFormGroup = FormGroup<SprintTaskHistoryFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class SprintTaskHistoryFormService {
  createSprintTaskHistoryFormGroup(sprintTaskHistory: SprintTaskHistoryFormGroupInput = { id: null }): SprintTaskHistoryFormGroup {
    const sprintTaskHistoryRawValue = {
      ...this.getFormDefaults(),
      ...sprintTaskHistory,
    };
    return new FormGroup<SprintTaskHistoryFormGroupContent>({
      id: new FormControl(
        { value: sprintTaskHistoryRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      comments: new FormControl(sprintTaskHistoryRawValue.comments),
      fromDate: new FormControl(sprintTaskHistoryRawValue.fromDate),
      toDate: new FormControl(sprintTaskHistoryRawValue.toDate),
      fromStatus: new FormControl(sprintTaskHistoryRawValue.fromStatus),
      toStatus: new FormControl(sprintTaskHistoryRawValue.toStatus),
    });
  }

  getSprintTaskHistory(form: SprintTaskHistoryFormGroup): ISprintTaskHistory | NewSprintTaskHistory {
    return form.getRawValue() as ISprintTaskHistory | NewSprintTaskHistory;
  }

  resetForm(form: SprintTaskHistoryFormGroup, sprintTaskHistory: SprintTaskHistoryFormGroupInput): void {
    const sprintTaskHistoryRawValue = { ...this.getFormDefaults(), ...sprintTaskHistory };
    form.reset(
      {
        ...sprintTaskHistoryRawValue,
        id: { value: sprintTaskHistoryRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): SprintTaskHistoryFormDefaults {
    return {
      id: null,
    };
  }
}
