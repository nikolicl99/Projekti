unit CallCentarOdgovor;

interface

uses
  Winapi.Windows, Winapi.Messages, System.SysUtils, System.Variants, System.Classes, Vcl.Graphics,
  Vcl.Controls, Vcl.Forms, Vcl.Dialogs, Vcl.StdCtrls, Vcl.Imaging.jpeg,
  Vcl.ExtCtrls, Vcl.ComCtrls, FireDAC.Stan.Intf, FireDAC.Stan.Option,
  FireDAC.Stan.Error, FireDAC.UI.Intf, FireDAC.Phys.Intf, FireDAC.Stan.Def,
  FireDAC.Stan.Pool, FireDAC.Stan.Async, FireDAC.Phys, FireDAC.Phys.SQLite,
  FireDAC.Phys.SQLiteDef, FireDAC.Stan.ExprFuncs,
  FireDAC.Phys.SQLiteWrapper.Stat, FireDAC.VCLUI.Wait, FireDAC.Stan.Param,
  FireDAC.DatS, FireDAC.DApt.Intf, FireDAC.DApt, Data.DB, FireDAC.Comp.DataSet,
  FireDAC.Comp.Client, Vcl.Imaging.pngimage;

type
  TCCForm2 = class(TForm)
    Image1: TImage;
    Label7: TLabel;
    Label8: TLabel;
    Label9: TLabel;
    Label10: TLabel;
    Label11: TLabel;
    Label12: TLabel;
    Label13: TLabel;
    Label1: TLabel;
    Label2: TLabel;
    Button1: TButton;
    Button2: TButton;
    Label3: TLabel;
    Label4: TLabel;
    RichEdit1: TRichEdit;
    Edit1: TEdit;
    Label5: TLabel;
    Label6: TLabel;
    Label14: TLabel;
    Edit2: TEdit;
    Edit3: TEdit;
    Image2: TImage;
    procedure Button2Click(Sender: TObject);
    procedure Button1Click(Sender: TObject);
  private
    { Private declarations }
  public
    { Public declarations }
  end;

var
  CCForm2: TCCForm2;

implementation

{$R *.dfm}

uses CallCentar, Vlasnik_GlavnaForma, Main;

procedure TCCForm2.Button1Click(Sender: TObject);
var
  SifraValidacije, Email: string;
  IDPitanja, IDVlasnika: Integer;
  MyQuery: TFDQuery;
begin
  SifraValidacije := Edit1.Text;
  Email := Edit2.Text;
  IDPitanja := StrToInt(Edit3.Text);
  MyQuery:= TFDQuery.Create(nil);
  MyQuery.Connection := GlobalConnection;

  // Proveravamo da li se uneta šifra validacije nalazi u tabeli Vlasnici i da li odgovara email adresi
  MyQuery.SQL.Clear;
  MyQuery.SQL.Add('SELECT * FROM Vlasnici WHERE Sifra_Validacije = :SifraValidacije AND Email = :Email');
  MyQuery.ParamByName('SifraValidacije').AsString := SifraValidacije;
  MyQuery.ParamByName('Email').AsString := Email;
  MyQuery.Open;

  if MyQuery.IsEmpty then
  begin
    // Ako nema podudaranja, prikazujemo poruku "Nema prolaza"
    ShowMessage('Uneli ste pogrešnu šifru za autentifikaciju, molimo Vas da se vratite i pokušate ponovo.');
  end
  else
  begin
    // Ako postoji podudaranje, provjeravamo IDPitanja putem IDVlasnika
//    MyQuery.SQL.Clear;
//    MyQuery.SQL.Text := 'SELECT LAST_INSERT_ROWID() AS IDVlasnika';
//    MyQuery.Open;
//    IDVlasnika := MyQuery.FieldByName('IDVlasnika').AsInteger;

    MyQuery.SQL.Clear;
    MyQuery.SQL.Add('SELECT * FROM Call_Centar WHERE IDPitanja = :IDPitanja');
    MyQuery.ParamByName('IDPitanja').AsInteger := IDPitanja;
//    MyQuery.ParamByName('IDKlijent').AsInteger := IDVlasnika;
    MyQuery.Open;

    if MyQuery.IsEmpty then
    begin
      // Ako nema podudaranja, prikazujemo poruku "IDPitanja nije povezan s Emailom"
      ShowMessage('Uneseni ID pitanja nije povezan s vašim Emailom.');
    end
    else
    begin
       // Ako postoji podudaranje, provjeravamo odgovor u tabeli Call_Centar

      if not MyQuery.FieldByName('Odgovor').IsNull then
      begin
        // Ako postoji odgovor, prikazujemo ga u RichEdit1 samo za traženo IDPitanje
        if MyQuery.FieldByName('IDPitanja').AsInteger = IDPitanja then
        begin
          RichEdit1.Text := MyQuery.FieldByName('Odgovor').AsString;
        end
        else
        begin
          RichEdit1.Text := 'Nema još uvek odgovora za traženo IDPitanje.';
        end;
      end
      else
      begin
        // Ako ne postoji odgovor, prikazujemo poruku "Nema još uvek odgovora" u RichEdit1
        RichEdit1.Text := 'Nema još uvek odgovora.';
      end;
    end;
  end;
end;

procedure TCCForm2.Button2Click(Sender: TObject);
begin
CCForm1.show;
self.hide;
end;

end.
