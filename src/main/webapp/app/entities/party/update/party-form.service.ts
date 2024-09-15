import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IParty, NewParty } from '../party.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IParty for edit and NewPartyFormGroupInput for create.
 */
type PartyFormGroupInput = IParty | PartialWithRequiredKeyOf<NewParty>;

type PartyFormDefaults = Pick<NewParty, 'id'>;

type PartyFormGroupContent = {
  id: FormControl<IParty['id'] | NewParty['id']>;
  firstName: FormControl<IParty['firstName']>;
  lastName: FormControl<IParty['lastName']>;
  displayName: FormControl<IParty['displayName']>;
  email: FormControl<IParty['email']>;
  dob: FormControl<IParty['dob']>;
  notes: FormControl<IParty['notes']>;
  mobileNumber: FormControl<IParty['mobileNumber']>;
  employeeId: FormControl<IParty['employeeId']>;
  login: FormControl<IParty['login']>;
  user: FormControl<IParty['user']>;
  status: FormControl<IParty['status']>;
  partyType: FormControl<IParty['partyType']>;
};

export type PartyFormGroup = FormGroup<PartyFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PartyFormService {
  createPartyFormGroup(party: PartyFormGroupInput = { id: null }): PartyFormGroup {
    const partyRawValue = {
      ...this.getFormDefaults(),
      ...party,
    };
    return new FormGroup<PartyFormGroupContent>({
      id: new FormControl(
        { value: partyRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      firstName: new FormControl(partyRawValue.firstName, {
        validators: [Validators.maxLength(100)],
      }),
      lastName: new FormControl(partyRawValue.lastName, {
        validators: [Validators.maxLength(100)],
      }),
      displayName: new FormControl(partyRawValue.displayName, {
        validators: [Validators.maxLength(100)],
      }),
      email: new FormControl(partyRawValue.email, {
        validators: [Validators.minLength(5), Validators.maxLength(75)],
      }),
      dob: new FormControl(partyRawValue.dob),
      notes: new FormControl(partyRawValue.notes, {
        validators: [Validators.maxLength(255)],
      }),
      mobileNumber: new FormControl(partyRawValue.mobileNumber, {
        validators: [Validators.required, Validators.minLength(10), Validators.maxLength(14)],
      }),
      employeeId: new FormControl(partyRawValue.employeeId),
      login: new FormControl(partyRawValue.login),
      user: new FormControl(partyRawValue.user),
      status: new FormControl(partyRawValue.status),
      partyType: new FormControl(partyRawValue.partyType),
    });
  }

  getParty(form: PartyFormGroup): IParty | NewParty {
    return form.getRawValue() as IParty | NewParty;
  }

  resetForm(form: PartyFormGroup, party: PartyFormGroupInput): void {
    const partyRawValue = { ...this.getFormDefaults(), ...party };
    form.reset(
      {
        ...partyRawValue,
        id: { value: partyRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): PartyFormDefaults {
    return {
      id: null,
    };
  }
}
