unit Pregled;

interface

uses
  System.SysUtils, System.Types, System.UITypes, System.Classes, System.Variants,
  FMX.Types, FMX.Controls, FMX.Forms, FMX.Graphics, FMX.Dialogs,
  FMX.Controls.Presentation, FMX.Edit, FMX.StdCtrls, FMX.Objects,
  FireDAC.Stan.Intf, FireDAC.Stan.Option, FireDAC.Stan.Param,
  FireDAC.Stan.Error, FireDAC.DatS, FireDAC.Phys.Intf, FireDAC.DApt.Intf,
  FireDAC.Stan.Async, FireDAC.DApt, Data.DB, FireDAC.Comp.DataSet,
  FireDAC.Comp.Client;

type
  TfrmPregled = class(TForm)
    Nazad: TButton;
    ImeLjubimca: TEdit;
    VrstaLjubimca: TEdit;
    ImeVlasnika: TEdit;
    Datum: TEdit;
    Vreme: TEdit;
    Simptomi: TEdit;
    Terapija: TEdit;
    PrezimeVlasnika: TEdit;
    ImeLjubimca_Label: TLabel;
    VrstaLjubimca_Label: TLabel;
    ImeVlasnika_Label: TLabel;
    PrezimeVlasnika_Label: TLabel;
    Datum_Label: TLabel;
    Vreme_Label: TLabel;
    Simptomi_Label: TLabel;
    Terapija_Label: TLabel;
    Lekovi: TButton;
    Navbar: TImage;
    StyleBook: TStyleBook;
    Forma: TPanel;
    Rasa: TEdit;
    Rasa_Label: TLabel;
    procedure NazadClick(Sender: TObject);
    procedure FormActivate(Sender: TObject);
    procedure LekoviClick(Sender: TObject);
  private
public
PregledID: integer;

  end;

var
  frmPregled: TfrmPregled;

implementation

{$R *.fmx}
uses Termini_Veterinar, Main, LoginZaposleni, PrepisaniLekovi, veterinarvakcine;

procedure TfrmPregled.FormActivate(Sender: TObject);
begin
    ImeLjubimca.Text := frmTerminiVeterinar.FSelectedImeLjubimca;
    VrstaLjubimca.Text := frmTerminiVeterinar.FSelectedVrstaLjubimca;
    Rasa.Text := frmTerminiVeterinar.FSelectedRasa;
    ImeVlasnika.Text := frmTerminiVeterinar.FSelectedImeVlasnika;
    PrezimeVlasnika.Text := frmTerminiVeterinar.FSelectedPrezimeVlasnika;
    Datum.Text := DateToStr(frmTerminiVeterinar.FSelectedDatum);
    Vreme.Text := TimeToStr(frmTerminiVeterinar.FSelectedVreme);
end;

procedure TfrmPregled.LekoviClick(Sender: TObject);
begin
var
MyQuery: TFDQuery;
begin
MyQuery := TFDQuery.Create(nil);
try
    MyQuery.Connection := GlobalConnection;
    MyQuery.SQL.Text := 'INSERT INTO PREGLED (TERMIN, SIMPTOMI, TERAPIJA, VETERINAR) VALUES (:termin, :simptomi, :terapija, :veterinar)';
    MyQuery.ParamByName('termin').AsInteger := frmTerminiVeterinar.FSelectedID;
    MyQuery.ParamByName('simptomi').AsString := Simptomi.Text;
    MyQuery.ParamByName('terapija').AsString := Terapija.text;
    MyQuery.ParamByName('veterinar').AsInteger := frmLogInZaposleni.UlogovaniZaposleniID;

     MyQuery.execsql;


    finally
    MyQuery.SQL.Text := 'SELECT last_insert_rowid()';
    MyQuery.Open;

    PregledID := MyQuery.Fields[0].AsInteger;
    MyQuery.Free;
    if frmterminiveterinar.Vakcinacija_bol then
    begin
      frmveterinarvakcine.show;
      self.hide;
    end
    else
    begin
     frmprepisanilekovi.show;
     self.hide;
    end;
end;
end;
end;

procedure TfrmPregled.NazadClick(Sender: TObject);
begin
frmTerminiVeterinar.show;
self.hide;
end;

end.
