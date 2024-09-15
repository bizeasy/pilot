import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import TaskHistoryResolve from './route/task-history-routing-resolve.service';

const taskHistoryRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/task-history.component').then(m => m.TaskHistoryComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/task-history-detail.component').then(m => m.TaskHistoryDetailComponent),
    resolve: {
      taskHistory: TaskHistoryResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/task-history-update.component').then(m => m.TaskHistoryUpdateComponent),
    resolve: {
      taskHistory: TaskHistoryResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/task-history-update.component').then(m => m.TaskHistoryUpdateComponent),
    resolve: {
      taskHistory: TaskHistoryResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default taskHistoryRoute;
