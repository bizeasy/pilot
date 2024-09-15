import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IRetrospective } from '../retrospective.model';
import { RetrospectiveService } from '../service/retrospective.service';

const retrospectiveResolve = (route: ActivatedRouteSnapshot): Observable<null | IRetrospective> => {
  const id = route.params.id;
  if (id) {
    return inject(RetrospectiveService)
      .find(id)
      .pipe(
        mergeMap((retrospective: HttpResponse<IRetrospective>) => {
          if (retrospective.body) {
            return of(retrospective.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default retrospectiveResolve;
