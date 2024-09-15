import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import TeamVelocityResolve from './route/team-velocity-routing-resolve.service';

const teamVelocityRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/team-velocity.component').then(m => m.TeamVelocityComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/team-velocity-detail.component').then(m => m.TeamVelocityDetailComponent),
    resolve: {
      teamVelocity: TeamVelocityResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/team-velocity-update.component').then(m => m.TeamVelocityUpdateComponent),
    resolve: {
      teamVelocity: TeamVelocityResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/team-velocity-update.component').then(m => m.TeamVelocityUpdateComponent),
    resolve: {
      teamVelocity: TeamVelocityResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default teamVelocityRoute;
