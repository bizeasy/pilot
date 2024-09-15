import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITaskHistory } from '../task-history.model';
import { TaskHistoryService } from '../service/task-history.service';

const taskHistoryResolve = (route: ActivatedRouteSnapshot): Observable<null | ITaskHistory> => {
  const id = route.params.id;
  if (id) {
    return inject(TaskHistoryService)
      .find(id)
      .pipe(
        mergeMap((taskHistory: HttpResponse<ITaskHistory>) => {
          if (taskHistory.body) {
            return of(taskHistory.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default taskHistoryResolve;
