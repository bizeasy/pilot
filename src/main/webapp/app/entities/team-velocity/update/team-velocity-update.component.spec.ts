import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { ISprint } from 'app/entities/sprint/sprint.model';
import { SprintService } from 'app/entities/sprint/service/sprint.service';
import { TeamVelocityService } from '../service/team-velocity.service';
import { ITeamVelocity } from '../team-velocity.model';
import { TeamVelocityFormService } from './team-velocity-form.service';

import { TeamVelocityUpdateComponent } from './team-velocity-update.component';

describe('TeamVelocity Management Update Component', () => {
  let comp: TeamVelocityUpdateComponent;
  let fixture: ComponentFixture<TeamVelocityUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let teamVelocityFormService: TeamVelocityFormService;
  let teamVelocityService: TeamVelocityService;
  let sprintService: SprintService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TeamVelocityUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(TeamVelocityUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TeamVelocityUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    teamVelocityFormService = TestBed.inject(TeamVelocityFormService);
    teamVelocityService = TestBed.inject(TeamVelocityService);
    sprintService = TestBed.inject(SprintService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Sprint query and add missing value', () => {
      const teamVelocity: ITeamVelocity = { id: 456 };
      const sprint: ISprint = { id: 25775 };
      teamVelocity.sprint = sprint;

      const sprintCollection: ISprint[] = [{ id: 21213 }];
      jest.spyOn(sprintService, 'query').mockReturnValue(of(new HttpResponse({ body: sprintCollection })));
      const additionalSprints = [sprint];
      const expectedCollection: ISprint[] = [...additionalSprints, ...sprintCollection];
      jest.spyOn(sprintService, 'addSprintToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ teamVelocity });
      comp.ngOnInit();

      expect(sprintService.query).toHaveBeenCalled();
      expect(sprintService.addSprintToCollectionIfMissing).toHaveBeenCalledWith(
        sprintCollection,
        ...additionalSprints.map(expect.objectContaining),
      );
      expect(comp.sprintsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const teamVelocity: ITeamVelocity = { id: 456 };
      const sprint: ISprint = { id: 27193 };
      teamVelocity.sprint = sprint;

      activatedRoute.data = of({ teamVelocity });
      comp.ngOnInit();

      expect(comp.sprintsSharedCollection).toContain(sprint);
      expect(comp.teamVelocity).toEqual(teamVelocity);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITeamVelocity>>();
      const teamVelocity = { id: 123 };
      jest.spyOn(teamVelocityFormService, 'getTeamVelocity').mockReturnValue(teamVelocity);
      jest.spyOn(teamVelocityService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ teamVelocity });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: teamVelocity }));
      saveSubject.complete();

      // THEN
      expect(teamVelocityFormService.getTeamVelocity).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(teamVelocityService.update).toHaveBeenCalledWith(expect.objectContaining(teamVelocity));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITeamVelocity>>();
      const teamVelocity = { id: 123 };
      jest.spyOn(teamVelocityFormService, 'getTeamVelocity').mockReturnValue({ id: null });
      jest.spyOn(teamVelocityService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ teamVelocity: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: teamVelocity }));
      saveSubject.complete();

      // THEN
      expect(teamVelocityFormService.getTeamVelocity).toHaveBeenCalled();
      expect(teamVelocityService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITeamVelocity>>();
      const teamVelocity = { id: 123 };
      jest.spyOn(teamVelocityService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ teamVelocity });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(teamVelocityService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareSprint', () => {
      it('Should forward to sprintService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(sprintService, 'compareSprint');
        comp.compareSprint(entity, entity2);
        expect(sprintService.compareSprint).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
