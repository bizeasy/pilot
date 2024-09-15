import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import SprintTaskHistoryResolve from './route/sprint-task-history-routing-resolve.service';

const sprintTaskHistoryRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/sprint-task-history.component').then(m => m.SprintTaskHistoryComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/sprint-task-history-detail.component').then(m => m.SprintTaskHistoryDetailComponent),
    resolve: {
      sprintTaskHistory: SprintTaskHistoryResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/sprint-task-history-update.component').then(m => m.SprintTaskHistoryUpdateComponent),
    resolve: {
      sprintTaskHistory: SprintTaskHistoryResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/sprint-task-history-update.component').then(m => m.SprintTaskHistoryUpdateComponent),
    resolve: {
      sprintTaskHistory: SprintTaskHistoryResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default sprintTaskHistoryRoute;
