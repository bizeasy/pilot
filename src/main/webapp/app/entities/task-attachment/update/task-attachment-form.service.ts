import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ITaskAttachment, NewTaskAttachment } from '../task-attachment.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITaskAttachment for edit and NewTaskAttachmentFormGroupInput for create.
 */
type TaskAttachmentFormGroupInput = ITaskAttachment | PartialWithRequiredKeyOf<NewTaskAttachment>;

type TaskAttachmentFormDefaults = Pick<NewTaskAttachment, 'id'>;

type TaskAttachmentFormGroupContent = {
  id: FormControl<ITaskAttachment['id'] | NewTaskAttachment['id']>;
  task: FormControl<ITaskAttachment['task']>;
  attachment: FormControl<ITaskAttachment['attachment']>;
};

export type TaskAttachmentFormGroup = FormGroup<TaskAttachmentFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TaskAttachmentFormService {
  createTaskAttachmentFormGroup(taskAttachment: TaskAttachmentFormGroupInput = { id: null }): TaskAttachmentFormGroup {
    const taskAttachmentRawValue = {
      ...this.getFormDefaults(),
      ...taskAttachment,
    };
    return new FormGroup<TaskAttachmentFormGroupContent>({
      id: new FormControl(
        { value: taskAttachmentRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      task: new FormControl(taskAttachmentRawValue.task),
      attachment: new FormControl(taskAttachmentRawValue.attachment),
    });
  }

  getTaskAttachment(form: TaskAttachmentFormGroup): ITaskAttachment | NewTaskAttachment {
    return form.getRawValue() as ITaskAttachment | NewTaskAttachment;
  }

  resetForm(form: TaskAttachmentFormGroup, taskAttachment: TaskAttachmentFormGroupInput): void {
    const taskAttachmentRawValue = { ...this.getFormDefaults(), ...taskAttachment };
    form.reset(
      {
        ...taskAttachmentRawValue,
        id: { value: taskAttachmentRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): TaskAttachmentFormDefaults {
    return {
      id: null,
    };
  }
}
