import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { ISprintAttachment } from '../sprint-attachment.model';

@Component({
  standalone: true,
  selector: 'sys-sprint-attachment-detail',
  templateUrl: './sprint-attachment-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class SprintAttachmentDetailComponent {
  sprintAttachment = input<ISprintAttachment | null>(null);

  previousState(): void {
    window.history.back();
  }
}
