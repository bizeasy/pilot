import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IParty } from 'app/entities/party/party.model';
import { PartyService } from 'app/entities/party/service/party.service';
import { IAttachment } from 'app/entities/attachment/attachment.model';
import { AttachmentService } from 'app/entities/attachment/service/attachment.service';
import { IPartyAttachment } from '../party-attachment.model';
import { PartyAttachmentService } from '../service/party-attachment.service';
import { PartyAttachmentFormService } from './party-attachment-form.service';

import { PartyAttachmentUpdateComponent } from './party-attachment-update.component';

describe('PartyAttachment Management Update Component', () => {
  let comp: PartyAttachmentUpdateComponent;
  let fixture: ComponentFixture<PartyAttachmentUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let partyAttachmentFormService: PartyAttachmentFormService;
  let partyAttachmentService: PartyAttachmentService;
  let partyService: PartyService;
  let attachmentService: AttachmentService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [PartyAttachmentUpdateComponent],
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
      .overrideTemplate(PartyAttachmentUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PartyAttachmentUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    partyAttachmentFormService = TestBed.inject(PartyAttachmentFormService);
    partyAttachmentService = TestBed.inject(PartyAttachmentService);
    partyService = TestBed.inject(PartyService);
    attachmentService = TestBed.inject(AttachmentService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Party query and add missing value', () => {
      const partyAttachment: IPartyAttachment = { id: 456 };
      const party: IParty = { id: 15515 };
      partyAttachment.party = party;

      const partyCollection: IParty[] = [{ id: 29701 }];
      jest.spyOn(partyService, 'query').mockReturnValue(of(new HttpResponse({ body: partyCollection })));
      const additionalParties = [party];
      const expectedCollection: IParty[] = [...additionalParties, ...partyCollection];
      jest.spyOn(partyService, 'addPartyToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ partyAttachment });
      comp.ngOnInit();

      expect(partyService.query).toHaveBeenCalled();
      expect(partyService.addPartyToCollectionIfMissing).toHaveBeenCalledWith(
        partyCollection,
        ...additionalParties.map(expect.objectContaining),
      );
      expect(comp.partiesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Attachment query and add missing value', () => {
      const partyAttachment: IPartyAttachment = { id: 456 };
      const attachment: IAttachment = { id: 332 };
      partyAttachment.attachment = attachment;

      const attachmentCollection: IAttachment[] = [{ id: 6572 }];
      jest.spyOn(attachmentService, 'query').mockReturnValue(of(new HttpResponse({ body: attachmentCollection })));
      const additionalAttachments = [attachment];
      const expectedCollection: IAttachment[] = [...additionalAttachments, ...attachmentCollection];
      jest.spyOn(attachmentService, 'addAttachmentToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ partyAttachment });
      comp.ngOnInit();

      expect(attachmentService.query).toHaveBeenCalled();
      expect(attachmentService.addAttachmentToCollectionIfMissing).toHaveBeenCalledWith(
        attachmentCollection,
        ...additionalAttachments.map(expect.objectContaining),
      );
      expect(comp.attachmentsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const partyAttachment: IPartyAttachment = { id: 456 };
      const party: IParty = { id: 13545 };
      partyAttachment.party = party;
      const attachment: IAttachment = { id: 27777 };
      partyAttachment.attachment = attachment;

      activatedRoute.data = of({ partyAttachment });
      comp.ngOnInit();

      expect(comp.partiesSharedCollection).toContain(party);
      expect(comp.attachmentsSharedCollection).toContain(attachment);
      expect(comp.partyAttachment).toEqual(partyAttachment);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPartyAttachment>>();
      const partyAttachment = { id: 123 };
      jest.spyOn(partyAttachmentFormService, 'getPartyAttachment').mockReturnValue(partyAttachment);
      jest.spyOn(partyAttachmentService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ partyAttachment });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: partyAttachment }));
      saveSubject.complete();

      // THEN
      expect(partyAttachmentFormService.getPartyAttachment).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(partyAttachmentService.update).toHaveBeenCalledWith(expect.objectContaining(partyAttachment));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPartyAttachment>>();
      const partyAttachment = { id: 123 };
      jest.spyOn(partyAttachmentFormService, 'getPartyAttachment').mockReturnValue({ id: null });
      jest.spyOn(partyAttachmentService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ partyAttachment: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: partyAttachment }));
      saveSubject.complete();

      // THEN
      expect(partyAttachmentFormService.getPartyAttachment).toHaveBeenCalled();
      expect(partyAttachmentService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPartyAttachment>>();
      const partyAttachment = { id: 123 };
      jest.spyOn(partyAttachmentService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ partyAttachment });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(partyAttachmentService.update).toHaveBeenCalled();
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

    describe('compareAttachment', () => {
      it('Should forward to attachmentService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(attachmentService, 'compareAttachment');
        comp.compareAttachment(entity, entity2);
        expect(attachmentService.compareAttachment).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
