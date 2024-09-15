import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import PartyResolve from './route/party-routing-resolve.service';

const partyRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/party.component').then(m => m.PartyComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/party-detail.component').then(m => m.PartyDetailComponent),
    resolve: {
      party: PartyResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/party-update.component').then(m => m.PartyUpdateComponent),
    resolve: {
      party: PartyResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/party-update.component').then(m => m.PartyUpdateComponent),
    resolve: {
      party: PartyResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default partyRoute;
