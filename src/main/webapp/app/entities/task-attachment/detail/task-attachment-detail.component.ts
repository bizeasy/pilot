import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { ITaskAttachment } from '../task-attachment.model';

@Component({
  standalone: true,
  selector: 'sys-task-attachment-detail',
  templateUrl: './task-attachment-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class TaskAttachmentDetailComponent {
  taskAttachment = input<ITaskAttachment | null>(null);

  previousState(): void {
    window.history.back();
  }
}
