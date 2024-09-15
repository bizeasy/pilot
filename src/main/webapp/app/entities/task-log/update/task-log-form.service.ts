import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ITaskLog, NewTaskLog } from '../task-log.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITaskLog for edit and NewTaskLogFormGroupInput for create.
 */
type TaskLogFormGroupInput = ITaskLog | PartialWithRequiredKeyOf<NewTaskLog>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ITaskLog | NewTaskLog> = Omit<T, 'fromTime' | 'toTime'> & {
  fromTime?: string | null;
  toTime?: string | null;
};

type TaskLogFormRawValue = FormValueOf<ITaskLog>;

type NewTaskLogFormRawValue = FormValueOf<NewTaskLog>;

type TaskLogFormDefaults = Pick<NewTaskLog, 'id' | 'fromTime' | 'toTime'>;

type TaskLogFormGroupContent = {
  id: FormControl<TaskLogFormRawValue['id'] | NewTaskLog['id']>;
  comments: FormControl<TaskLogFormRawValue['comments']>;
  fromTime: FormControl<TaskLogFormRawValue['fromTime']>;
  toTime: FormControl<TaskLogFormRawValue['toTime']>;
  task: FormControl<TaskLogFormRawValue['task']>;
};

export type TaskLogFormGroup = FormGroup<TaskLogFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TaskLogFormService {
  createTaskLogFormGroup(taskLog: TaskLogFormGroupInput = { id: null }): TaskLogFormGroup {
    const taskLogRawValue = this.convertTaskLogToTaskLogRawValue({
      ...this.getFormDefaults(),
      ...taskLog,
    });
    return new FormGroup<TaskLogFormGroupContent>({
      id: new FormControl(
        { value: taskLogRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      comments: new FormControl(taskLogRawValue.comments),
      fromTime: new FormControl(taskLogRawValue.fromTime),
      toTime: new FormControl(taskLogRawValue.toTime),
      task: new FormControl(taskLogRawValue.task),
    });
  }

  getTaskLog(form: TaskLogFormGroup): ITaskLog | NewTaskLog {
    return this.convertTaskLogRawValueToTaskLog(form.getRawValue() as TaskLogFormRawValue | NewTaskLogFormRawValue);
  }

  resetForm(form: TaskLogFormGroup, taskLog: TaskLogFormGroupInput): void {
    const taskLogRawValue = this.convertTaskLogToTaskLogRawValue({ ...this.getFormDefaults(), ...taskLog });
    form.reset(
      {
        ...taskLogRawValue,
        id: { value: taskLogRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): TaskLogFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      fromTime: currentTime,
      toTime: currentTime,
    };
  }

  private convertTaskLogRawValueToTaskLog(rawTaskLog: TaskLogFormRawValue | NewTaskLogFormRawValue): ITaskLog | NewTaskLog {
    return {
      ...rawTaskLog,
      fromTime: dayjs(rawTaskLog.fromTime, DATE_TIME_FORMAT),
      toTime: dayjs(rawTaskLog.toTime, DATE_TIME_FORMAT),
    };
  }

  private convertTaskLogToTaskLogRawValue(
    taskLog: ITaskLog | (Partial<NewTaskLog> & TaskLogFormDefaults),
  ): TaskLogFormRawValue | PartialWithRequiredKeyOf<NewTaskLogFormRawValue> {
    return {
      ...taskLog,
      fromTime: taskLog.fromTime ? taskLog.fromTime.format(DATE_TIME_FORMAT) : undefined,
      toTime: taskLog.toTime ? taskLog.toTime.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
