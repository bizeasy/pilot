import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ITask, NewTask } from '../task.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITask for edit and NewTaskFormGroupInput for create.
 */
type TaskFormGroupInput = ITask | PartialWithRequiredKeyOf<NewTask>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ITask | NewTask> = Omit<T, 'startTime' | 'pauseTime' | 'endTime'> & {
  startTime?: string | null;
  pauseTime?: string | null;
  endTime?: string | null;
};

type TaskFormRawValue = FormValueOf<ITask>;

type NewTaskFormRawValue = FormValueOf<NewTask>;

type TaskFormDefaults = Pick<NewTask, 'id' | 'startTime' | 'pauseTime' | 'endTime'>;

type TaskFormGroupContent = {
  id: FormControl<TaskFormRawValue['id'] | NewTask['id']>;
  title: FormControl<TaskFormRawValue['title']>;
  description: FormControl<TaskFormRawValue['description']>;
  priority: FormControl<TaskFormRawValue['priority']>;
  dueDate: FormControl<TaskFormRawValue['dueDate']>;
  info: FormControl<TaskFormRawValue['info']>;
  storyPoints: FormControl<TaskFormRawValue['storyPoints']>;
  startTime: FormControl<TaskFormRawValue['startTime']>;
  pauseTime: FormControl<TaskFormRawValue['pauseTime']>;
  endTime: FormControl<TaskFormRawValue['endTime']>;
  totalDuration: FormControl<TaskFormRawValue['totalDuration']>;
  sequenceNo: FormControl<TaskFormRawValue['sequenceNo']>;
  project: FormControl<TaskFormRawValue['project']>;
  status: FormControl<TaskFormRawValue['status']>;
  assignee: FormControl<TaskFormRawValue['assignee']>;
};

export type TaskFormGroup = FormGroup<TaskFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TaskFormService {
  createTaskFormGroup(task: TaskFormGroupInput = { id: null }): TaskFormGroup {
    const taskRawValue = this.convertTaskToTaskRawValue({
      ...this.getFormDefaults(),
      ...task,
    });
    return new FormGroup<TaskFormGroupContent>({
      id: new FormControl(
        { value: taskRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      title: new FormControl(taskRawValue.title, {
        validators: [Validators.required],
      }),
      description: new FormControl(taskRawValue.description),
      priority: new FormControl(taskRawValue.priority),
      dueDate: new FormControl(taskRawValue.dueDate),
      info: new FormControl(taskRawValue.info),
      storyPoints: new FormControl(taskRawValue.storyPoints),
      startTime: new FormControl(taskRawValue.startTime),
      pauseTime: new FormControl(taskRawValue.pauseTime),
      endTime: new FormControl(taskRawValue.endTime),
      totalDuration: new FormControl(taskRawValue.totalDuration),
      sequenceNo: new FormControl(taskRawValue.sequenceNo),
      project: new FormControl(taskRawValue.project, {
        validators: [Validators.required],
      }),
      status: new FormControl(taskRawValue.status),
      assignee: new FormControl(taskRawValue.assignee),
    });
  }

  getTask(form: TaskFormGroup): ITask | NewTask {
    return this.convertTaskRawValueToTask(form.getRawValue() as TaskFormRawValue | NewTaskFormRawValue);
  }

  resetForm(form: TaskFormGroup, task: TaskFormGroupInput): void {
    const taskRawValue = this.convertTaskToTaskRawValue({ ...this.getFormDefaults(), ...task });
    form.reset(
      {
        ...taskRawValue,
        id: { value: taskRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): TaskFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      startTime: currentTime,
      pauseTime: currentTime,
      endTime: currentTime,
    };
  }

  private convertTaskRawValueToTask(rawTask: TaskFormRawValue | NewTaskFormRawValue): ITask | NewTask {
    return {
      ...rawTask,
      startTime: dayjs(rawTask.startTime, DATE_TIME_FORMAT),
      pauseTime: dayjs(rawTask.pauseTime, DATE_TIME_FORMAT),
      endTime: dayjs(rawTask.endTime, DATE_TIME_FORMAT),
    };
  }

  private convertTaskToTaskRawValue(
    task: ITask | (Partial<NewTask> & TaskFormDefaults),
  ): TaskFormRawValue | PartialWithRequiredKeyOf<NewTaskFormRawValue> {
    return {
      ...task,
      startTime: task.startTime ? task.startTime.format(DATE_TIME_FORMAT) : undefined,
      pauseTime: task.pauseTime ? task.pauseTime.format(DATE_TIME_FORMAT) : undefined,
      endTime: task.endTime ? task.endTime.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
