import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ISprintAttachment, NewSprintAttachment } from '../sprint-attachment.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ISprintAttachment for edit and NewSprintAttachmentFormGroupInput for create.
 */
type SprintAttachmentFormGroupInput = ISprintAttachment | PartialWithRequiredKeyOf<NewSprintAttachment>;

type SprintAttachmentFormDefaults = Pick<NewSprintAttachment, 'id'>;

type SprintAttachmentFormGroupContent = {
  id: FormControl<ISprintAttachment['id'] | NewSprintAttachment['id']>;
  sprint: FormControl<ISprintAttachment['sprint']>;
  attachment: FormControl<ISprintAttachment['attachment']>;
};

export type SprintAttachmentFormGroup = FormGroup<SprintAttachmentFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class SprintAttachmentFormService {
  createSprintAttachmentFormGroup(sprintAttachment: SprintAttachmentFormGroupInput = { id: null }): SprintAttachmentFormGroup {
    const sprintAttachmentRawValue = {
      ...this.getFormDefaults(),
      ...sprintAttachment,
    };
    return new FormGroup<SprintAttachmentFormGroupContent>({
      id: new FormControl(
        { value: sprintAttachmentRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      sprint: new FormControl(sprintAttachmentRawValue.sprint),
      attachment: new FormControl(sprintAttachmentRawValue.attachment),
    });
  }

  getSprintAttachment(form: SprintAttachmentFormGroup): ISprintAttachment | NewSprintAttachment {
    return form.getRawValue() as ISprintAttachment | NewSprintAttachment;
  }

  resetForm(form: SprintAttachmentFormGroup, sprintAttachment: SprintAttachmentFormGroupInput): void {
    const sprintAttachmentRawValue = { ...this.getFormDefaults(), ...sprintAttachment };
    form.reset(
      {
        ...sprintAttachmentRawValue,
        id: { value: sprintAttachmentRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): SprintAttachmentFormDefaults {
    return {
      id: null,
    };
  }
}
