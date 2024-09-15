import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ISprintTask, NewSprintTask } from '../sprint-task.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ISprintTask for edit and NewSprintTaskFormGroupInput for create.
 */
type SprintTaskFormGroupInput = ISprintTask | PartialWithRequiredKeyOf<NewSprintTask>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ISprintTask | NewSprintTask> = Omit<T, 'fromTime' | 'thruTime' | 'assignedTime'> & {
  fromTime?: string | null;
  thruTime?: string | null;
  assignedTime?: string | null;
};

type SprintTaskFormRawValue = FormValueOf<ISprintTask>;

type NewSprintTaskFormRawValue = FormValueOf<NewSprintTask>;

type SprintTaskFormDefaults = Pick<NewSprintTask, 'id' | 'fromTime' | 'thruTime' | 'assignedTime'>;

type SprintTaskFormGroupContent = {
  id: FormControl<SprintTaskFormRawValue['id'] | NewSprintTask['id']>;
  sequenceNo: FormControl<SprintTaskFormRawValue['sequenceNo']>;
  storyPoints: FormControl<SprintTaskFormRawValue['storyPoints']>;
  fromTime: FormControl<SprintTaskFormRawValue['fromTime']>;
  thruTime: FormControl<SprintTaskFormRawValue['thruTime']>;
  assignedTime: FormControl<SprintTaskFormRawValue['assignedTime']>;
  duration: FormControl<SprintTaskFormRawValue['duration']>;
  task: FormControl<SprintTaskFormRawValue['task']>;
  sprint: FormControl<SprintTaskFormRawValue['sprint']>;
  assignedTo: FormControl<SprintTaskFormRawValue['assignedTo']>;
  assignedBy: FormControl<SprintTaskFormRawValue['assignedBy']>;
  qa: FormControl<SprintTaskFormRawValue['qa']>;
  reviewedBy: FormControl<SprintTaskFormRawValue['reviewedBy']>;
  status: FormControl<SprintTaskFormRawValue['status']>;
};

export type SprintTaskFormGroup = FormGroup<SprintTaskFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class SprintTaskFormService {
  createSprintTaskFormGroup(sprintTask: SprintTaskFormGroupInput = { id: null }): SprintTaskFormGroup {
    const sprintTaskRawValue = this.convertSprintTaskToSprintTaskRawValue({
      ...this.getFormDefaults(),
      ...sprintTask,
    });
    return new FormGroup<SprintTaskFormGroupContent>({
      id: new FormControl(
        { value: sprintTaskRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      sequenceNo: new FormControl(sprintTaskRawValue.sequenceNo),
      storyPoints: new FormControl(sprintTaskRawValue.storyPoints),
      fromTime: new FormControl(sprintTaskRawValue.fromTime),
      thruTime: new FormControl(sprintTaskRawValue.thruTime),
      assignedTime: new FormControl(sprintTaskRawValue.assignedTime),
      duration: new FormControl(sprintTaskRawValue.duration),
      task: new FormControl(sprintTaskRawValue.task),
      sprint: new FormControl(sprintTaskRawValue.sprint),
      assignedTo: new FormControl(sprintTaskRawValue.assignedTo),
      assignedBy: new FormControl(sprintTaskRawValue.assignedBy),
      qa: new FormControl(sprintTaskRawValue.qa),
      reviewedBy: new FormControl(sprintTaskRawValue.reviewedBy),
      status: new FormControl(sprintTaskRawValue.status),
    });
  }

  getSprintTask(form: SprintTaskFormGroup): ISprintTask | NewSprintTask {
    return this.convertSprintTaskRawValueToSprintTask(form.getRawValue() as SprintTaskFormRawValue | NewSprintTaskFormRawValue);
  }

  resetForm(form: SprintTaskFormGroup, sprintTask: SprintTaskFormGroupInput): void {
    const sprintTaskRawValue = this.convertSprintTaskToSprintTaskRawValue({ ...this.getFormDefaults(), ...sprintTask });
    form.reset(
      {
        ...sprintTaskRawValue,
        id: { value: sprintTaskRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): SprintTaskFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      fromTime: currentTime,
      thruTime: currentTime,
      assignedTime: currentTime,
    };
  }

  private convertSprintTaskRawValueToSprintTask(
    rawSprintTask: SprintTaskFormRawValue | NewSprintTaskFormRawValue,
  ): ISprintTask | NewSprintTask {
    return {
      ...rawSprintTask,
      fromTime: dayjs(rawSprintTask.fromTime, DATE_TIME_FORMAT),
      thruTime: dayjs(rawSprintTask.thruTime, DATE_TIME_FORMAT),
      assignedTime: dayjs(rawSprintTask.assignedTime, DATE_TIME_FORMAT),
    };
  }

  private convertSprintTaskToSprintTaskRawValue(
    sprintTask: ISprintTask | (Partial<NewSprintTask> & SprintTaskFormDefaults),
  ): SprintTaskFormRawValue | PartialWithRequiredKeyOf<NewSprintTaskFormRawValue> {
    return {
      ...sprintTask,
      fromTime: sprintTask.fromTime ? sprintTask.fromTime.format(DATE_TIME_FORMAT) : undefined,
      thruTime: sprintTask.thruTime ? sprintTask.thruTime.format(DATE_TIME_FORMAT) : undefined,
      assignedTime: sprintTask.assignedTime ? sprintTask.assignedTime.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
