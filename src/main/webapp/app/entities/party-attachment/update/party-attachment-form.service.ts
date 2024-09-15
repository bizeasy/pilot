import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IPartyAttachment, NewPartyAttachment } from '../party-attachment.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPartyAttachment for edit and NewPartyAttachmentFormGroupInput for create.
 */
type PartyAttachmentFormGroupInput = IPartyAttachment | PartialWithRequiredKeyOf<NewPartyAttachment>;

type PartyAttachmentFormDefaults = Pick<NewPartyAttachment, 'id'>;

type PartyAttachmentFormGroupContent = {
  id: FormControl<IPartyAttachment['id'] | NewPartyAttachment['id']>;
  party: FormControl<IPartyAttachment['party']>;
  attachment: FormControl<IPartyAttachment['attachment']>;
};

export type PartyAttachmentFormGroup = FormGroup<PartyAttachmentFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PartyAttachmentFormService {
  createPartyAttachmentFormGroup(partyAttachment: PartyAttachmentFormGroupInput = { id: null }): PartyAttachmentFormGroup {
    const partyAttachmentRawValue = {
      ...this.getFormDefaults(),
      ...partyAttachment,
    };
    return new FormGroup<PartyAttachmentFormGroupContent>({
      id: new FormControl(
        { value: partyAttachmentRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      party: new FormControl(partyAttachmentRawValue.party),
      attachment: new FormControl(partyAttachmentRawValue.attachment),
    });
  }

  getPartyAttachment(form: PartyAttachmentFormGroup): IPartyAttachment | NewPartyAttachment {
    return form.getRawValue() as IPartyAttachment | NewPartyAttachment;
  }

  resetForm(form: PartyAttachmentFormGroup, partyAttachment: PartyAttachmentFormGroupInput): void {
    const partyAttachmentRawValue = { ...this.getFormDefaults(), ...partyAttachment };
    form.reset(
      {
        ...partyAttachmentRawValue,
        id: { value: partyAttachmentRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): PartyAttachmentFormDefaults {
    return {
      id: null,
    };
  }
}
