import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { IIndividualPerformance } from '../individual-performance.model';

@Component({
  standalone: true,
  selector: 'sys-individual-performance-detail',
  templateUrl: './individual-performance-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class IndividualPerformanceDetailComponent {
  individualPerformance = input<IIndividualPerformance | null>(null);

  previousState(): void {
    window.history.back();
  }
}
