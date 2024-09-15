import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IPartyType, NewPartyType } from '../party-type.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPartyType for edit and NewPartyTypeFormGroupInput for create.
 */
type PartyTypeFormGroupInput = IPartyType | PartialWithRequiredKeyOf<NewPartyType>;

type PartyTypeFormDefaults = Pick<NewPartyType, 'id'>;

type PartyTypeFormGroupContent = {
  id: FormControl<IPartyType['id'] | NewPartyType['id']>;
  name: FormControl<IPartyType['name']>;
  description: FormControl<IPartyType['description']>;
};

export type PartyTypeFormGroup = FormGroup<PartyTypeFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PartyTypeFormService {
  createPartyTypeFormGroup(partyType: PartyTypeFormGroupInput = { id: null }): PartyTypeFormGroup {
    const partyTypeRawValue = {
      ...this.getFormDefaults(),
      ...partyType,
    };
    return new FormGroup<PartyTypeFormGroupContent>({
      id: new FormControl(
        { value: partyTypeRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(partyTypeRawValue.name, {
        validators: [Validators.maxLength(60)],
      }),
      description: new FormControl(partyTypeRawValue.description, {
        validators: [Validators.maxLength(255)],
      }),
    });
  }

  getPartyType(form: PartyTypeFormGroup): IPartyType | NewPartyType {
    return form.getRawValue() as IPartyType | NewPartyType;
  }

  resetForm(form: PartyTypeFormGroup, partyType: PartyTypeFormGroupInput): void {
    const partyTypeRawValue = { ...this.getFormDefaults(), ...partyType };
    form.reset(
      {
        ...partyTypeRawValue,
        id: { value: partyTypeRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): PartyTypeFormDefaults {
    return {
      id: null,
    };
  }
}
