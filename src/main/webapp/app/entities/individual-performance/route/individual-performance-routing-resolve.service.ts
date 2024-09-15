import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IIndividualPerformance } from '../individual-performance.model';
import { IndividualPerformanceService } from '../service/individual-performance.service';

const individualPerformanceResolve = (route: ActivatedRouteSnapshot): Observable<null | IIndividualPerformance> => {
  const id = route.params.id;
  if (id) {
    return inject(IndividualPerformanceService)
      .find(id)
      .pipe(
        mergeMap((individualPerformance: HttpResponse<IIndividualPerformance>) => {
          if (individualPerformance.body) {
            return of(individualPerformance.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default individualPerformanceResolve;
