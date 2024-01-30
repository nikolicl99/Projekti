unit CallCentar;

interface

uses
  Winapi.Windows, Winapi.Messages, System.SysUtils, System.Variants, System.Classes, Vcl.Graphics,
  Vcl.Controls, Vcl.Forms, Vcl.Dialogs, Vcl.StdCtrls, Vcl.ComCtrls,
  FireDAC.Stan.Intf, FireDAC.Stan.Option, FireDAC.Stan.Error, FireDAC.UI.Intf,
  FireDAC.Phys.Intf, FireDAC.Stan.Def, FireDAC.Stan.Pool, FireDAC.Stan.Async,
  FireDAC.Phys, FireDAC.Phys.SQLite, FireDAC.Phys.SQLiteDef,
  FireDAC.Stan.ExprFuncs, FireDAC.Phys.SQLiteWrapper.Stat, FireDAC.VCLUI.Wait,
  FireDAC.Stan.Param, FireDAC.DatS, FireDAC.DApt.Intf, FireDAC.DApt, Data.DB,
  FireDAC.Comp.DataSet, FireDAC.Comp.Client, Vcl.MPlayer, Vcl.Imaging.jpeg,
  Vcl.ExtCtrls, Vcl.Imaging.pngimage;

type
  TCCForm1 = class(TForm)
    Label1: TLabel;
    Label2: TLabel;
    Label3: TLabel;
    Edit1: TEdit;
    Button1: TButton;
    Button2: TButton;
    Label4: TLabel;
    Label5: TLabel;
    RadioButton2: TRadioButton;
    RadioButton1: TRadioButton;
    RichEdit1: TRichEdit;
    Label6: TLabel;
    Edit2: TEdit;
    Image1: TImage;
    Label7: TLabel;
    Label8: TLabel;
    Label9: TLabel;
    Label10: TLabel;
    Label11: TLabel;
    Label12: TLabel;
    Label13: TLabel;
    Label14: TLabel;
    Button3: TButton;
    Button4: TButton;
    Image3: TImage;
    procedure RadioButton1Click(Sender: TObject);
    procedure RadioButton2Click(Sender: TObject);
    procedure Button2Click(Sender: TObject);
    procedure Button1Click(Sender: TObject);
    procedure Button3Click(Sender: TObject);
    procedure Button4Click(Sender: TObject);
  private
    { Private declarations }
  public
    { Public declarations }
  end;

var
  CCForm1: TCCForm1;

implementation

{$R *.dfm}

uses CallCentarOdgovor, CallCentarOdgovorVeterinara, Vlasnik_GlavnaForma, Main;

procedure TCCForm1.Button1Click(Sender: TObject);
var
  SifraValidacije, Email: string;
  TipPitanja, Pitanje: string;
  IDKlijent: Integer;
  IDPitanja: Integer;
  MyQuery: TFDQuery;
begin
  // Dobijamo vrednosti unete u Edit1, Edit2 i RichEdit1 komponentama
  SifraValidacije := Edit1.Text;
  Email := Edit2.Text;
  Pitanje := RichEdit1.Text;
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
    // Ako postoji podudaranje, prikazujemo poruku "Prolaz" i unosimo podatke u tabelu Call_Centar
    var
    MessageText: string;
    MessageText := 'Vreme slanja: ' + TimeToStr(Time) + sLineBreak + 'Vaša poruka je uspešno poslata.';
    ShowMessage(MessageText);

    // Odreðujemo vrednost polja "Tip_Pitanja" na osnovu stanja RadioButton1 i RadioButton2
    if RadioButton1.Checked then
      TipPitanja := 'PORUKA'
    else if RadioButton2.Checked then
      TipPitanja := 'POZIV'
    else
      TipPitanja := '';

    // Dobijamo vrednost polja "IDVlasnika" za red koji odgovara unetom emailu
    IDKlijent := MyQuery.FieldByName('IDVlasnika').AsInteger;

    MyQuery.SQL.Text := 'INSERT INTO Call_Centar (IDKlijent, Tip_Pitanja, Datum_Prijema, Vreme_Prijema, Pitanje) VALUES (:IDKlijent, :Tip_Pitanja, :Datum_Prijema, :Vreme_Prijema, :Pitanje)';
    MyQuery.ParamByName('IDKlijent').AsInteger := IDKlijent;
    MyQuery.ParamByName('Tip_Pitanja').AsString := TipPitanja;
    MyQuery.ParamByName('Datum_Prijema').AsString := DateToStr(Date); // Trenutni datum
    MyQuery.ParamByName('Vreme_Prijema').AsString := TimeToStr(Time); // Trenutno vreme
    MyQuery.ParamByName('Pitanje').AsString := Pitanje;

    MyQuery.ExecSQL;
    // Nastavite sa unosom ostalih polja potrebnih za tabelu Call_Centar, izuzev polja Datum_Odgovora, Vreme_Odgovora i Odgovor
//    MyQuery.Post;

    // Prikazujemo poruku sa ID pitanja
    MyQuery.SQL.Text := 'SELECT LAST_INSERT_ROWID() AS IDPitanja';
    MyQuery.Open;
    IDPitanja := MyQuery.FieldByName('IDPitanja').AsInteger;
    ShowMessage('ID Vašeg pitanja je: ' + IntToStr(IDPitanja));
  end;
end;


procedure TCCForm1.Button2Click(Sender: TObject);
begin
frmglavnimeni.show;
self.hide;
end;

procedure TCCForm1.Button3Click(Sender: TObject);
var
  SifraValidacije, Email: string;
  TipPitanja, Pitanje: string;
  IDKlijent: Integer;
  IDPitanja:Integer;
  MyQuery: TFDQuery;
begin
  // Dobijamo vrednosti unete u Edit1, Edit2 i RichEdit1 komponentama
  SifraValidacije := Edit1.Text;
  Email := Edit2.Text;
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
    // Ako postoji podudaranje, prikazujemo poruku "Prolaz" i unosimo podatke u tabelu Call_Centar
    var
    MessageText: string;
    MessageText := 'Vreme poziva: ' + TimeToStr(Time) + sLineBreak + 'Uspešno ste otpoceli poziv.';
    ShowMessage(MessageText);
    // Odreðujemo vrednost polja "Tip_Pitanja" na osnovu stanja RadioButton1 i RadioButton2
    if RadioButton1.Checked then
      TipPitanja := 'PORUKA'
    else if RadioButton2.Checked then
      TipPitanja := 'POZIV'
    else
      TipPitanja := '';

    // Dobijamo vrednost polja "IDVlasnika" za red koji odgovara unetom emailu
    IDKlijent := MyQuery.FieldByName('IDVlasnika').AsInteger;
    MyQuery.SQL.Text := 'INSERT INTO Call_Centar (IDKlijent, Tip_Pitanja, Datum_Prijema, Vreme_Prijema) VALUES (:IDKlijent, :Tip_Pitanja, :Datum_Prijema, :Vreme_Prijema)';
    MyQuery.ParamByName('IDKlijent').AsInteger := IDKlijent;
    MyQuery.ParamByName('Tip_Pitanja').AsString := TipPitanja;
    MyQuery.ParamByName('Datum_Prijema').AsString := DateToStr(Date); // Trenutni datum
    MyQuery.ParamByName('Vreme_Prijema').AsString := TimeToStr(Time); // Trenutno vreme

    MyQuery.ExecSQL;

    // Nastavite sa unosom ostalih polja potrebnih za tabelu Call_Centar, izuzev polja Datum_Odgovora, Vreme_Odgovora i Odgovor
//    MyQuery.Post;
      MyQuery.SQL.Text := 'SELECT LAST_INSERT_ROWID() AS IDPitanja';
    MyQuery.Open;
     IDPitanja := MyQuery.FieldByName('IDPitanja').AsInteger;
    ShowMessage('ID Vašeg poziva je: ' + IntToStr(IDPitanja));
  end;
end;


procedure TCCForm1.Button4Click(Sender: TObject);
begin
CCForm2.Show;
self.hide;
end;


procedure TCCForm1.RadioButton1Click(Sender: TObject);
begin
  if RadioButton1.Checked then
  begin
    Button3.Visible:= False;
    Label5.Visible := True;
    Button1.Visible := True;
    RichEdit1.Visible := True;
    Label14.Visible:=True;
  end
  else
  begin
    Label5.Visible := False;
    Button1.Visible := False;
    RichEdit1.Visible := False;
  end;
end;

procedure TCCForm1.RadioButton2Click(Sender: TObject);
begin
  if RadioButton2.Checked then
  begin

    Button1.Visible :=False;
    Button3.Visible:= True;
    Label5.Visible := False;
    RichEdit1.Visible := False;
    Label14.Visible:=True;
  end;
end;

end.

