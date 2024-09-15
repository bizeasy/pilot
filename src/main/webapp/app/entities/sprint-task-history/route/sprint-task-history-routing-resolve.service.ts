import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISprintTaskHistory } from '../sprint-task-history.model';
import { SprintTaskHistoryService } from '../service/sprint-task-history.service';

const sprintTaskHistoryResolve = (route: ActivatedRouteSnapshot): Observable<null | ISprintTaskHistory> => {
  const id = route.params.id;
  if (id) {
    return inject(SprintTaskHistoryService)
      .find(id)
      .pipe(
        mergeMap((sprintTaskHistory: HttpResponse<ISprintTaskHistory>) => {
          if (sprintTaskHistory.body) {
            return of(sprintTaskHistory.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default sprintTaskHistoryResolve;
