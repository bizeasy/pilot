import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IStatusCategory, NewStatusCategory } from '../status-category.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IStatusCategory for edit and NewStatusCategoryFormGroupInput for create.
 */
type StatusCategoryFormGroupInput = IStatusCategory | PartialWithRequiredKeyOf<NewStatusCategory>;

type StatusCategoryFormDefaults = Pick<NewStatusCategory, 'id'>;

type StatusCategoryFormGroupContent = {
  id: FormControl<IStatusCategory['id'] | NewStatusCategory['id']>;
  name: FormControl<IStatusCategory['name']>;
  description: FormControl<IStatusCategory['description']>;
};

export type StatusCategoryFormGroup = FormGroup<StatusCategoryFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class StatusCategoryFormService {
  createStatusCategoryFormGroup(statusCategory: StatusCategoryFormGroupInput = { id: null }): StatusCategoryFormGroup {
    const statusCategoryRawValue = {
      ...this.getFormDefaults(),
      ...statusCategory,
    };
    return new FormGroup<StatusCategoryFormGroupContent>({
      id: new FormControl(
        { value: statusCategoryRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(statusCategoryRawValue.name, {
        validators: [Validators.maxLength(60)],
      }),
      description: new FormControl(statusCategoryRawValue.description, {
        validators: [Validators.maxLength(100)],
      }),
    });
  }

  getStatusCategory(form: StatusCategoryFormGroup): IStatusCategory | NewStatusCategory {
    return form.getRawValue() as IStatusCategory | NewStatusCategory;
  }

  resetForm(form: StatusCategoryFormGroup, statusCategory: StatusCategoryFormGroupInput): void {
    const statusCategoryRawValue = { ...this.getFormDefaults(), ...statusCategory };
    form.reset(
      {
        ...statusCategoryRawValue,
        id: { value: statusCategoryRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): StatusCategoryFormDefaults {
    return {
      id: null,
    };
  }
}
