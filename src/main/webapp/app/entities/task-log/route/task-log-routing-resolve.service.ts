import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITaskLog } from '../task-log.model';
import { TaskLogService } from '../service/task-log.service';

const taskLogResolve = (route: ActivatedRouteSnapshot): Observable<null | ITaskLog> => {
  const id = route.params.id;
  if (id) {
    return inject(TaskLogService)
      .find(id)
      .pipe(
        mergeMap((taskLog: HttpResponse<ITaskLog>) => {
          if (taskLog.body) {
            return of(taskLog.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default taskLogResolve;
