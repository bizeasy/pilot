import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IParty } from 'app/entities/party/party.model';
import { PartyService } from 'app/entities/party/service/party.service';
import { ISprint } from 'app/entities/sprint/sprint.model';
import { SprintService } from 'app/entities/sprint/service/sprint.service';
import { IIndividualPerformance } from '../individual-performance.model';
import { IndividualPerformanceService } from '../service/individual-performance.service';
import { IndividualPerformanceFormService } from './individual-performance-form.service';

import { IndividualPerformanceUpdateComponent } from './individual-performance-update.component';

describe('IndividualPerformance Management Update Component', () => {
  let comp: IndividualPerformanceUpdateComponent;
  let fixture: ComponentFixture<IndividualPerformanceUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let individualPerformanceFormService: IndividualPerformanceFormService;
  let individualPerformanceService: IndividualPerformanceService;
  let partyService: PartyService;
  let sprintService: SprintService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [IndividualPerformanceUpdateComponent],
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
      .overrideTemplate(IndividualPerformanceUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(IndividualPerformanceUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    individualPerformanceFormService = TestBed.inject(IndividualPerformanceFormService);
    individualPerformanceService = TestBed.inject(IndividualPerformanceService);
    partyService = TestBed.inject(PartyService);
    sprintService = TestBed.inject(SprintService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Party query and add missing value', () => {
      const individualPerformance: IIndividualPerformance = { id: 456 };
      const party: IParty = { id: 16749 };
      individualPerformance.party = party;

      const partyCollection: IParty[] = [{ id: 18617 }];
      jest.spyOn(partyService, 'query').mockReturnValue(of(new HttpResponse({ body: partyCollection })));
      const additionalParties = [party];
      const expectedCollection: IParty[] = [...additionalParties, ...partyCollection];
      jest.spyOn(partyService, 'addPartyToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ individualPerformance });
      comp.ngOnInit();

      expect(partyService.query).toHaveBeenCalled();
      expect(partyService.addPartyToCollectionIfMissing).toHaveBeenCalledWith(
        partyCollection,
        ...additionalParties.map(expect.objectContaining),
      );
      expect(comp.partiesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Sprint query and add missing value', () => {
      const individualPerformance: IIndividualPerformance = { id: 456 };
      const sprint: ISprint = { id: 4622 };
      individualPerformance.sprint = sprint;

      const sprintCollection: ISprint[] = [{ id: 24583 }];
      jest.spyOn(sprintService, 'query').mockReturnValue(of(new HttpResponse({ body: sprintCollection })));
      const additionalSprints = [sprint];
      const expectedCollection: ISprint[] = [...additionalSprints, ...sprintCollection];
      jest.spyOn(sprintService, 'addSprintToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ individualPerformance });
      comp.ngOnInit();

      expect(sprintService.query).toHaveBeenCalled();
      expect(sprintService.addSprintToCollectionIfMissing).toHaveBeenCalledWith(
        sprintCollection,
        ...additionalSprints.map(expect.objectContaining),
      );
      expect(comp.sprintsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const individualPerformance: IIndividualPerformance = { id: 456 };
      const party: IParty = { id: 5755 };
      individualPerformance.party = party;
      const sprint: ISprint = { id: 8501 };
      individualPerformance.sprint = sprint;

      activatedRoute.data = of({ individualPerformance });
      comp.ngOnInit();

      expect(comp.partiesSharedCollection).toContain(party);
      expect(comp.sprintsSharedCollection).toContain(sprint);
      expect(comp.individualPerformance).toEqual(individualPerformance);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IIndividualPerformance>>();
      const individualPerformance = { id: 123 };
      jest.spyOn(individualPerformanceFormService, 'getIndividualPerformance').mockReturnValue(individualPerformance);
      jest.spyOn(individualPerformanceService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ individualPerformance });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: individualPerformance }));
      saveSubject.complete();

      // THEN
      expect(individualPerformanceFormService.getIndividualPerformance).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(individualPerformanceService.update).toHaveBeenCalledWith(expect.objectContaining(individualPerformance));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IIndividualPerformance>>();
      const individualPerformance = { id: 123 };
      jest.spyOn(individualPerformanceFormService, 'getIndividualPerformance').mockReturnValue({ id: null });
      jest.spyOn(individualPerformanceService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ individualPerformance: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: individualPerformance }));
      saveSubject.complete();

      // THEN
      expect(individualPerformanceFormService.getIndividualPerformance).toHaveBeenCalled();
      expect(individualPerformanceService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IIndividualPerformance>>();
      const individualPerformance = { id: 123 };
      jest.spyOn(individualPerformanceService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ individualPerformance });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(individualPerformanceService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareParty', () => {
      it('Should forward to partyService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(partyService, 'compareParty');
        comp.compareParty(entity, entity2);
        expect(partyService.compareParty).toHaveBeenCalledWith(entity, entity2);
      });
    });

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
