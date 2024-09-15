import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IIndividualPerformance, NewIndividualPerformance } from '../individual-performance.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IIndividualPerformance for edit and NewIndividualPerformanceFormGroupInput for create.
 */
type IndividualPerformanceFormGroupInput = IIndividualPerformance | PartialWithRequiredKeyOf<NewIndividualPerformance>;

type IndividualPerformanceFormDefaults = Pick<NewIndividualPerformance, 'id'>;

type IndividualPerformanceFormGroupContent = {
  id: FormControl<IIndividualPerformance['id'] | NewIndividualPerformance['id']>;
  completedTasks: FormControl<IIndividualPerformance['completedTasks']>;
  velocity: FormControl<IIndividualPerformance['velocity']>;
  storyPointsCompleted: FormControl<IIndividualPerformance['storyPointsCompleted']>;
  party: FormControl<IIndividualPerformance['party']>;
  sprint: FormControl<IIndividualPerformance['sprint']>;
};

export type IndividualPerformanceFormGroup = FormGroup<IndividualPerformanceFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class IndividualPerformanceFormService {
  createIndividualPerformanceFormGroup(
    individualPerformance: IndividualPerformanceFormGroupInput = { id: null },
  ): IndividualPerformanceFormGroup {
    const individualPerformanceRawValue = {
      ...this.getFormDefaults(),
      ...individualPerformance,
    };
    return new FormGroup<IndividualPerformanceFormGroupContent>({
      id: new FormControl(
        { value: individualPerformanceRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      completedTasks: new FormControl(individualPerformanceRawValue.completedTasks),
      velocity: new FormControl(individualPerformanceRawValue.velocity),
      storyPointsCompleted: new FormControl(individualPerformanceRawValue.storyPointsCompleted),
      party: new FormControl(individualPerformanceRawValue.party),
      sprint: new FormControl(individualPerformanceRawValue.sprint),
    });
  }

  getIndividualPerformance(form: IndividualPerformanceFormGroup): IIndividualPerformance | NewIndividualPerformance {
    return form.getRawValue() as IIndividualPerformance | NewIndividualPerformance;
  }

  resetForm(form: IndividualPerformanceFormGroup, individualPerformance: IndividualPerformanceFormGroupInput): void {
    const individualPerformanceRawValue = { ...this.getFormDefaults(), ...individualPerformance };
    form.reset(
      {
        ...individualPerformanceRawValue,
        id: { value: individualPerformanceRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): IndividualPerformanceFormDefaults {
    return {
      id: null,
    };
  }
}
