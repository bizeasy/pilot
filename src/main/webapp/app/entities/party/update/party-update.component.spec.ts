import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { IStatus } from 'app/entities/status/status.model';
import { StatusService } from 'app/entities/status/service/status.service';
import { IPartyType } from 'app/entities/party-type/party-type.model';
import { PartyTypeService } from 'app/entities/party-type/service/party-type.service';
import { IParty } from '../party.model';
import { PartyService } from '../service/party.service';
import { PartyFormService } from './party-form.service';

import { PartyUpdateComponent } from './party-update.component';

describe('Party Management Update Component', () => {
  let comp: PartyUpdateComponent;
  let fixture: ComponentFixture<PartyUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let partyFormService: PartyFormService;
  let partyService: PartyService;
  let userService: UserService;
  let statusService: StatusService;
  let partyTypeService: PartyTypeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [PartyUpdateComponent],
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
      .overrideTemplate(PartyUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PartyUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    partyFormService = TestBed.inject(PartyFormService);
    partyService = TestBed.inject(PartyService);
    userService = TestBed.inject(UserService);
    statusService = TestBed.inject(StatusService);
    partyTypeService = TestBed.inject(PartyTypeService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const party: IParty = { id: 456 };
      const user: IUser = { id: 15299 };
      party.user = user;

      const userCollection: IUser[] = [{ id: 30783 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ party });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining),
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Status query and add missing value', () => {
      const party: IParty = { id: 456 };
      const status: IStatus = { id: 1110 };
      party.status = status;

      const statusCollection: IStatus[] = [{ id: 17879 }];
      jest.spyOn(statusService, 'query').mockReturnValue(of(new HttpResponse({ body: statusCollection })));
      const additionalStatuses = [status];
      const expectedCollection: IStatus[] = [...additionalStatuses, ...statusCollection];
      jest.spyOn(statusService, 'addStatusToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ party });
      comp.ngOnInit();

      expect(statusService.query).toHaveBeenCalled();
      expect(statusService.addStatusToCollectionIfMissing).toHaveBeenCalledWith(
        statusCollection,
        ...additionalStatuses.map(expect.objectContaining),
      );
      expect(comp.statusesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call PartyType query and add missing value', () => {
      const party: IParty = { id: 456 };
      const partyType: IPartyType = { id: 18618 };
      party.partyType = partyType;

      const partyTypeCollection: IPartyType[] = [{ id: 760 }];
      jest.spyOn(partyTypeService, 'query').mockReturnValue(of(new HttpResponse({ body: partyTypeCollection })));
      const additionalPartyTypes = [partyType];
      const expectedCollection: IPartyType[] = [...additionalPartyTypes, ...partyTypeCollection];
      jest.spyOn(partyTypeService, 'addPartyTypeToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ party });
      comp.ngOnInit();

      expect(partyTypeService.query).toHaveBeenCalled();
      expect(partyTypeService.addPartyTypeToCollectionIfMissing).toHaveBeenCalledWith(
        partyTypeCollection,
        ...additionalPartyTypes.map(expect.objectContaining),
      );
      expect(comp.partyTypesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const party: IParty = { id: 456 };
      const user: IUser = { id: 9081 };
      party.user = user;
      const status: IStatus = { id: 10383 };
      party.status = status;
      const partyType: IPartyType = { id: 31257 };
      party.partyType = partyType;

      activatedRoute.data = of({ party });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContain(user);
      expect(comp.statusesSharedCollection).toContain(status);
      expect(comp.partyTypesSharedCollection).toContain(partyType);
      expect(comp.party).toEqual(party);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IParty>>();
      const party = { id: 123 };
      jest.spyOn(partyFormService, 'getParty').mockReturnValue(party);
      jest.spyOn(partyService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ party });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: party }));
      saveSubject.complete();

      // THEN
      expect(partyFormService.getParty).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(partyService.update).toHaveBeenCalledWith(expect.objectContaining(party));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IParty>>();
      const party = { id: 123 };
      jest.spyOn(partyFormService, 'getParty').mockReturnValue({ id: null });
      jest.spyOn(partyService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ party: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: party }));
      saveSubject.complete();

      // THEN
      expect(partyFormService.getParty).toHaveBeenCalled();
      expect(partyService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IParty>>();
      const party = { id: 123 };
      jest.spyOn(partyService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ party });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(partyService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUser', () => {
      it('Should forward to userService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareStatus', () => {
      it('Should forward to statusService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(statusService, 'compareStatus');
        comp.compareStatus(entity, entity2);
        expect(statusService.compareStatus).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('comparePartyType', () => {
      it('Should forward to partyTypeService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(partyTypeService, 'comparePartyType');
        comp.comparePartyType(entity, entity2);
        expect(partyTypeService.comparePartyType).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
