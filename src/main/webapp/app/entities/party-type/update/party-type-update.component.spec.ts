import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { PartyTypeService } from '../service/party-type.service';
import { IPartyType } from '../party-type.model';
import { PartyTypeFormService } from './party-type-form.service';

import { PartyTypeUpdateComponent } from './party-type-update.component';

describe('PartyType Management Update Component', () => {
  let comp: PartyTypeUpdateComponent;
  let fixture: ComponentFixture<PartyTypeUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let partyTypeFormService: PartyTypeFormService;
  let partyTypeService: PartyTypeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [PartyTypeUpdateComponent],
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
      .overrideTemplate(PartyTypeUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PartyTypeUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    partyTypeFormService = TestBed.inject(PartyTypeFormService);
    partyTypeService = TestBed.inject(PartyTypeService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const partyType: IPartyType = { id: 456 };

      activatedRoute.data = of({ partyType });
      comp.ngOnInit();

      expect(comp.partyType).toEqual(partyType);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPartyType>>();
      const partyType = { id: 123 };
      jest.spyOn(partyTypeFormService, 'getPartyType').mockReturnValue(partyType);
      jest.spyOn(partyTypeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ partyType });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: partyType }));
      saveSubject.complete();

      // THEN
      expect(partyTypeFormService.getPartyType).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(partyTypeService.update).toHaveBeenCalledWith(expect.objectContaining(partyType));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPartyType>>();
      const partyType = { id: 123 };
      jest.spyOn(partyTypeFormService, 'getPartyType').mockReturnValue({ id: null });
      jest.spyOn(partyTypeService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ partyType: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: partyType }));
      saveSubject.complete();

      // THEN
      expect(partyTypeFormService.getPartyType).toHaveBeenCalled();
      expect(partyTypeService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPartyType>>();
      const partyType = { id: 123 };
      jest.spyOn(partyTypeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ partyType });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(partyTypeService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
