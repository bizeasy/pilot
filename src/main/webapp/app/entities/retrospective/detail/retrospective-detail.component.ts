import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { IRetrospective } from '../retrospective.model';

@Component({
  standalone: true,
  selector: 'sys-retrospective-detail',
  templateUrl: './retrospective-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class RetrospectiveDetailComponent {
  retrospective = input<IRetrospective | null>(null);

  previousState(): void {
    window.history.back();
  }
}
