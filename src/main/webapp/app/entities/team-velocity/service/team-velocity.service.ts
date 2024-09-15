import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITeamVelocity, NewTeamVelocity } from '../team-velocity.model';

export type PartialUpdateTeamVelocity = Partial<ITeamVelocity> & Pick<ITeamVelocity, 'id'>;

export type EntityResponseType = HttpResponse<ITeamVelocity>;
export type EntityArrayResponseType = HttpResponse<ITeamVelocity[]>;

@Injectable({ providedIn: 'root' })
export class TeamVelocityService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/team-velocities');

  create(teamVelocity: NewTeamVelocity): Observable<EntityResponseType> {
    return this.http.post<ITeamVelocity>(this.resourceUrl, teamVelocity, { observe: 'response' });
  }

  update(teamVelocity: ITeamVelocity): Observable<EntityResponseType> {
    return this.http.put<ITeamVelocity>(`${this.resourceUrl}/${this.getTeamVelocityIdentifier(teamVelocity)}`, teamVelocity, {
      observe: 'response',
    });
  }

  partialUpdate(teamVelocity: PartialUpdateTeamVelocity): Observable<EntityResponseType> {
    return this.http.patch<ITeamVelocity>(`${this.resourceUrl}/${this.getTeamVelocityIdentifier(teamVelocity)}`, teamVelocity, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ITeamVelocity>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITeamVelocity[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getTeamVelocityIdentifier(teamVelocity: Pick<ITeamVelocity, 'id'>): number {
    return teamVelocity.id;
  }

  compareTeamVelocity(o1: Pick<ITeamVelocity, 'id'> | null, o2: Pick<ITeamVelocity, 'id'> | null): boolean {
    return o1 && o2 ? this.getTeamVelocityIdentifier(o1) === this.getTeamVelocityIdentifier(o2) : o1 === o2;
  }

  addTeamVelocityToCollectionIfMissing<Type extends Pick<ITeamVelocity, 'id'>>(
    teamVelocityCollection: Type[],
    ...teamVelocitiesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const teamVelocities: Type[] = teamVelocitiesToCheck.filter(isPresent);
    if (teamVelocities.length > 0) {
      const teamVelocityCollectionIdentifiers = teamVelocityCollection.map(teamVelocityItem =>
        this.getTeamVelocityIdentifier(teamVelocityItem),
      );
      const teamVelocitiesToAdd = teamVelocities.filter(teamVelocityItem => {
        const teamVelocityIdentifier = this.getTeamVelocityIdentifier(teamVelocityItem);
        if (teamVelocityCollectionIdentifiers.includes(teamVelocityIdentifier)) {
          return false;
        }
        teamVelocityCollectionIdentifiers.push(teamVelocityIdentifier);
        return true;
      });
      return [...teamVelocitiesToAdd, ...teamVelocityCollection];
    }
    return teamVelocityCollection;
  }
}
