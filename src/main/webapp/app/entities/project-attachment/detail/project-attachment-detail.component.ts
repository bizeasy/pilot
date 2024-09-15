import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { IProjectAttachment } from '../project-attachment.model';

@Component({
  standalone: true,
  selector: 'sys-project-attachment-detail',
  templateUrl: './project-attachment-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class ProjectAttachmentDetailComponent {
  projectAttachment = input<IProjectAttachment | null>(null);

  previousState(): void {
    window.history.back();
  }
}
