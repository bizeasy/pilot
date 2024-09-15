import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IRetrospective, NewRetrospective } from '../retrospective.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IRetrospective for edit and NewRetrospectiveFormGroupInput for create.
 */
type RetrospectiveFormGroupInput = IRetrospective | PartialWithRequiredKeyOf<NewRetrospective>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IRetrospective | NewRetrospective> = Omit<T, 'dateCreated'> & {
  dateCreated?: string | null;
};

type RetrospectiveFormRawValue = FormValueOf<IRetrospective>;

type NewRetrospectiveFormRawValue = FormValueOf<NewRetrospective>;

type RetrospectiveFormDefaults = Pick<NewRetrospective, 'id' | 'dateCreated'>;

type RetrospectiveFormGroupContent = {
  id: FormControl<RetrospectiveFormRawValue['id'] | NewRetrospective['id']>;
  summary: FormControl<RetrospectiveFormRawValue['summary']>;
  actionItems: FormControl<RetrospectiveFormRawValue['actionItems']>;
  dateCreated: FormControl<RetrospectiveFormRawValue['dateCreated']>;
  sprint: FormControl<RetrospectiveFormRawValue['sprint']>;
};

export type RetrospectiveFormGroup = FormGroup<RetrospectiveFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class RetrospectiveFormService {
  createRetrospectiveFormGroup(retrospective: RetrospectiveFormGroupInput = { id: null }): RetrospectiveFormGroup {
    const retrospectiveRawValue = this.convertRetrospectiveToRetrospectiveRawValue({
      ...this.getFormDefaults(),
      ...retrospective,
    });
    return new FormGroup<RetrospectiveFormGroupContent>({
      id: new FormControl(
        { value: retrospectiveRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      summary: new FormControl(retrospectiveRawValue.summary),
      actionItems: new FormControl(retrospectiveRawValue.actionItems),
      dateCreated: new FormControl(retrospectiveRawValue.dateCreated, {
        validators: [Validators.required],
      }),
      sprint: new FormControl(retrospectiveRawValue.sprint),
    });
  }

  getRetrospective(form: RetrospectiveFormGroup): IRetrospective | NewRetrospective {
    return this.convertRetrospectiveRawValueToRetrospective(form.getRawValue() as RetrospectiveFormRawValue | NewRetrospectiveFormRawValue);
  }

  resetForm(form: RetrospectiveFormGroup, retrospective: RetrospectiveFormGroupInput): void {
    const retrospectiveRawValue = this.convertRetrospectiveToRetrospectiveRawValue({ ...this.getFormDefaults(), ...retrospective });
    form.reset(
      {
        ...retrospectiveRawValue,
        id: { value: retrospectiveRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): RetrospectiveFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      dateCreated: currentTime,
    };
  }

  private convertRetrospectiveRawValueToRetrospective(
    rawRetrospective: RetrospectiveFormRawValue | NewRetrospectiveFormRawValue,
  ): IRetrospective | NewRetrospective {
    return {
      ...rawRetrospective,
      dateCreated: dayjs(rawRetrospective.dateCreated, DATE_TIME_FORMAT),
    };
  }

  private convertRetrospectiveToRetrospectiveRawValue(
    retrospective: IRetrospective | (Partial<NewRetrospective> & RetrospectiveFormDefaults),
  ): RetrospectiveFormRawValue | PartialWithRequiredKeyOf<NewRetrospectiveFormRawValue> {
    return {
      ...retrospective,
      dateCreated: retrospective.dateCreated ? retrospective.dateCreated.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
