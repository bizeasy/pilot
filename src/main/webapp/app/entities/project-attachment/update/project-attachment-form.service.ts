import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IProjectAttachment, NewProjectAttachment } from '../project-attachment.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IProjectAttachment for edit and NewProjectAttachmentFormGroupInput for create.
 */
type ProjectAttachmentFormGroupInput = IProjectAttachment | PartialWithRequiredKeyOf<NewProjectAttachment>;

type ProjectAttachmentFormDefaults = Pick<NewProjectAttachment, 'id'>;

type ProjectAttachmentFormGroupContent = {
  id: FormControl<IProjectAttachment['id'] | NewProjectAttachment['id']>;
  facility: FormControl<IProjectAttachment['facility']>;
  attachment: FormControl<IProjectAttachment['attachment']>;
};

export type ProjectAttachmentFormGroup = FormGroup<ProjectAttachmentFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ProjectAttachmentFormService {
  createProjectAttachmentFormGroup(projectAttachment: ProjectAttachmentFormGroupInput = { id: null }): ProjectAttachmentFormGroup {
    const projectAttachmentRawValue = {
      ...this.getFormDefaults(),
      ...projectAttachment,
    };
    return new FormGroup<ProjectAttachmentFormGroupContent>({
      id: new FormControl(
        { value: projectAttachmentRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      facility: new FormControl(projectAttachmentRawValue.facility),
      attachment: new FormControl(projectAttachmentRawValue.attachment),
    });
  }

  getProjectAttachment(form: ProjectAttachmentFormGroup): IProjectAttachment | NewProjectAttachment {
    return form.getRawValue() as IProjectAttachment | NewProjectAttachment;
  }

  resetForm(form: ProjectAttachmentFormGroup, projectAttachment: ProjectAttachmentFormGroupInput): void {
    const projectAttachmentRawValue = { ...this.getFormDefaults(), ...projectAttachment };
    form.reset(
      {
        ...projectAttachmentRawValue,
        id: { value: projectAttachmentRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): ProjectAttachmentFormDefaults {
    return {
      id: null,
    };
  }
}
