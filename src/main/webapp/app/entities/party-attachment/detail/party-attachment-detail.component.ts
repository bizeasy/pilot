import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { IPartyAttachment } from '../party-attachment.model';

@Component({
  standalone: true,
  selector: 'sys-party-attachment-detail',
  templateUrl: './party-attachment-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class PartyAttachmentDetailComponent {
  partyAttachment = input<IPartyAttachment | null>(null);

  previousState(): void {
    window.history.back();
  }
}
