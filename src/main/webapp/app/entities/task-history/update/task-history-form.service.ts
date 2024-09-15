import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ITaskHistory, NewTaskHistory } from '../task-history.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITaskHistory for edit and NewTaskHistoryFormGroupInput for create.
 */
type TaskHistoryFormGroupInput = ITaskHistory | PartialWithRequiredKeyOf<NewTaskHistory>;

type TaskHistoryFormDefaults = Pick<NewTaskHistory, 'id'>;

type TaskHistoryFormGroupContent = {
  id: FormControl<ITaskHistory['id'] | NewTaskHistory['id']>;
  type: FormControl<ITaskHistory['type']>;
  assignedTo: FormControl<ITaskHistory['assignedTo']>;
  sprint: FormControl<ITaskHistory['sprint']>;
  assignedBy: FormControl<ITaskHistory['assignedBy']>;
};

export type TaskHistoryFormGroup = FormGroup<TaskHistoryFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TaskHistoryFormService {
  createTaskHistoryFormGroup(taskHistory: TaskHistoryFormGroupInput = { id: null }): TaskHistoryFormGroup {
    const taskHistoryRawValue = {
      ...this.getFormDefaults(),
      ...taskHistory,
    };
    return new FormGroup<TaskHistoryFormGroupContent>({
      id: new FormControl(
        { value: taskHistoryRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      type: new FormControl(taskHistoryRawValue.type),
      assignedTo: new FormControl(taskHistoryRawValue.assignedTo),
      sprint: new FormControl(taskHistoryRawValue.sprint),
      assignedBy: new FormControl(taskHistoryRawValue.assignedBy),
    });
  }

  getTaskHistory(form: TaskHistoryFormGroup): ITaskHistory | NewTaskHistory {
    return form.getRawValue() as ITaskHistory | NewTaskHistory;
  }

  resetForm(form: TaskHistoryFormGroup, taskHistory: TaskHistoryFormGroupInput): void {
    const taskHistoryRawValue = { ...this.getFormDefaults(), ...taskHistory };
    form.reset(
      {
        ...taskHistoryRawValue,
        id: { value: taskHistoryRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): TaskHistoryFormDefaults {
    return {
      id: null,
    };
  }
}
